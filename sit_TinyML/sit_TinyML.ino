#include <Arduino.h>
#include <math.h>
#include <Arduino_LSM6DS3.h>

/*
  1-SENSOR POSTURE ML (AUTO SITTING/STANDING + NORMAL/IDEAL)

  Serial Monitor @115200:

  A = record STANDING NORMAL  8s
  B = record STANDING IDEAL   8s  (+ auto-set stand motion threshold)
  C = record SITTING NORMAL   8s
  D = record SITTING IDEAL    8s  (+ auto-set sit motion threshold)

  L = live mode ON
  S = live mode OFF


*/

struct PrototypeVec {
  float mean[4];
  float stdev[4];
  bool ready;
};

enum State { GOOD=0, BAD_PENDING=1, VIBRATING=2, COOLDOWN=3 };
enum PostureFamily { FAMILY_STAND=0, FAMILY_SIT=1 };

// ---------- Pins / Timing ----------
const int VIB_PIN = 6;

const unsigned long SAMPLE_MS   = 200;   // 5 samples/sec
const unsigned long BAD_HOLD_MS = 3000;  // must stay bad for 3 sec
const unsigned long COOLDOWN_MS = 7000;  // cooldown after vibration

// ---------- Decision tuning ----------
const float START_MARGIN = 0.25f;
const float STOP_MARGIN  = 0.10f;

// family switching margin:
// new family must be clearly better by this much before switch
const float FAMILY_MARGIN = 0.75f;

// how many consecutive samples before accepting a family switch
const int FAMILY_STABLE_N = 3;

// ---------- Prototypes ----------
PrototypeVec P_stand_normal = { {0,0,0,0}, {1,1,1,1}, false };
PrototypeVec P_stand_ideal  = { {0,0,0,0}, {1,1,1,1}, false };
PrototypeVec P_sit_normal   = { {0,0,0,0}, {1,1,1,1}, false };
PrototypeVec P_sit_ideal    = { {0,0,0,0}, {1,1,1,1}, false };

// ---------- Motion thresholds ----------
float motion_threshold_stand = 999.0f;
float motion_threshold_sit   = 999.0f;
bool motionHigh = false;

// ---------- Runtime ----------
State state = GOOD;
unsigned long lastSample = 0;
unsigned long badStart = 0;
unsigned long cooldownStart = 0;

bool liveMode = false;

// ---------- Family smoothing ----------
PostureFamily currentFamily = FAMILY_STAND;
PostureFamily candidateFamily = FAMILY_STAND;
int candidateCount = 0;

// ---------- Helpers ----------
static void computePitchRoll(float ax, float ay, float az, float &pitchDeg, float &rollDeg) {
  rollDeg  = atan2f(ay, az) * 180.0f / M_PI;
  pitchDeg = atan2f(-ax, sqrtf(ay * ay + az * az)) * 180.0f / M_PI;
}

static const char* stateLabel(State s) {
  switch(s) {
    case GOOD: return "GOOD";
    case BAD_PENDING: return "BAD_PENDING";
    case VIBRATING: return "VIBRATING";
    case COOLDOWN: return "COOLDOWN";
  }
  return "?";
}

static const char* familyLabel(PostureFamily f) {
  switch(f) {
    case FAMILY_STAND: return "STAND";
    case FAMILY_SIT:   return "SIT";
  }
  return "?";
}

static void setVibration(bool on) {
  digitalWrite(VIB_PIN, on ? HIGH : LOW);
  digitalWrite(LED_BUILTIN, on ? HIGH : LOW);
}

static bool readPitchRoll(float &pitch, float &roll) {
  if (!IMU.accelerationAvailable()) return false;

  float ax, ay, az;
  IMU.readAcceleration(ax, ay, az);
  computePitchRoll(ax, ay, az, pitch, roll);
  return true;
}

// Motion metric: prefer gyro magnitude; fallback to accel magnitude deviation
static float readCurrentMotion() {
  if (IMU.gyroscopeAvailable()) {
    float gx, gy, gz;
    IMU.readGyroscope(gx, gy, gz);
    return sqrtf(gx*gx + gy*gy + gz*gz);
  }

  if (IMU.accelerationAvailable()) {
    float ax, ay, az;
    IMU.readAcceleration(ax, ay, az);
    float amag = sqrtf(ax*ax + ay*ay + az*az); // ~1.0 when still
    return fabsf(amag - 1.0f) * 10.0f;
  }

  return 0.0f;
}

/*
  Same feature style as your old working code:
  x[0] = pitch
  x[1] = roll
  x[2] = pitch^2
  x[3] = roll^2
*/
static bool readCurrentFeatureVec(float x[4], float &pitchOut, float &rollOut) {
  float pitch, roll;
  if (!readPitchRoll(pitch, roll)) return false;

  x[0] = pitch;
  x[1] = roll;
  x[2] = pitch * pitch;
  x[3] = roll  * roll;

  pitchOut = pitch;
  rollOut  = roll;
  return true;
}

static float normDist4(const float x[4], const PrototypeVec &P) {
  float sum = 0.0f;
  for (int i=0; i<4; i++) {
    float sd = (P.stdev[i] < 0.001f) ? 0.001f : P.stdev[i];
    float z = (x[i] - P.mean[i]) / sd;
    sum += z * z;
  }
  return sqrtf(sum);
}

static void recordPrototype8s(PrototypeVec &P, const char *label, bool recordMotionToo, float *motionThresholdTarget) {
  const unsigned long DURATION_MS = 8000;

  float sum[4]   = {0,0,0,0};
  float sumsq[4] = {0,0,0,0};
  int n = 0;
  float maxStillMotion = 0.0f;

  unsigned long start = millis();
  while (millis() - start < DURATION_MS) {
    float x[4];
    float pitch = 0, roll = 0;

    if (readCurrentFeatureVec(x, pitch, roll)) {
      for (int i=0; i<4; i++) {
        sum[i] += x[i];
        sumsq[i] += x[i] * x[i];
      }
      n++;

      if (recordMotionToo) {
        float m = readCurrentMotion();
        if (m > maxStillMotion) maxStillMotion = m;
      }
    }

    delay(SAMPLE_MS);
  }

  if (n < 5) {
    Serial.println("Not enough IMU samples captured. Try again.");
    return;
  }

  for (int i=0; i<4; i++) {
    float mean = sum[i] / n;
    float var  = (sumsq[i] / n) - (mean * mean);
    if (var < 1e-6f) var = 1e-6f;
    P.mean[i]  = mean;
    P.stdev[i] = sqrtf(var);
  }

  P.ready = true;

  Serial.print("Recorded ");
  Serial.print(label);
  Serial.print(" prototype. Samples=");
  Serial.println(n);

  if (recordMotionToo && motionThresholdTarget != nullptr) {
    *motionThresholdTarget = maxStillMotion * 2.0f;
    if (*motionThresholdTarget < 12.0f) *motionThresholdTarget = 12.0f;
    Serial.print("Motion threshold for ");
    Serial.print(label);
    Serial.print(" = ");
    Serial.println(*motionThresholdTarget, 2);
  }
}

static bool allPrototypesReady() {
  return P_stand_normal.ready &&
         P_stand_ideal.ready  &&
         P_sit_normal.ready   &&
         P_sit_ideal.ready;
}

// choose family using only stand ideal vs sit ideal
static PostureFamily updateFamily(float dStandI, float dSitI) {
  PostureFamily rawFamily = currentFamily;

  if (dStandI + FAMILY_MARGIN < dSitI) {
    rawFamily = FAMILY_STAND;
  } else if (dSitI + FAMILY_MARGIN < dStandI) {
    rawFamily = FAMILY_SIT;
  } else {
    rawFamily = currentFamily; // too close -> keep old family
  }

  if (rawFamily == currentFamily) {
    candidateFamily = currentFamily;
    candidateCount = 0;
    return currentFamily;
  }

  if (rawFamily != candidateFamily) {
    candidateFamily = rawFamily;
    candidateCount = 1;
  } else {
    candidateCount++;
  }

  if (candidateCount >= FAMILY_STABLE_N) {
    currentFamily = candidateFamily;
    candidateCount = 0;
  }

  return currentFamily;
}

static void liveStepOnce() {
  if (!allPrototypesReady()) {
    Serial.println("Record all 4 prototypes first: A, B, C, D");
    return;
  }

  if (motion_threshold_stand > 900.0f || motion_threshold_sit > 900.0f) {
    Serial.println("Record B and D first so motion thresholds get set.");
    return;
  }

  float x[4];
  float pitch = 0, roll = 0;
  if (!readCurrentFeatureVec(x, pitch, roll)) return;

  // distances to all 4 prototypes
  float dStandN = normDist4(x, P_stand_normal);
  float dStandI = normDist4(x, P_stand_ideal);
  float dSitN   = normDist4(x, P_sit_normal);
  float dSitI   = normDist4(x, P_sit_ideal);

  // STEP 1: automatic family detection using only ideals
  PostureFamily family = updateFamily(dStandI, dSitI);

  // STEP 2: inside chosen family, use same old working logic
  float dNorm, dIdeal, motionThreshold;

  if (family == FAMILY_STAND) {
    dNorm = dStandN;
    dIdeal = dStandI;
    motionThreshold = motion_threshold_stand;
  } else {
    dNorm = dSitN;
    dIdeal = dSitI;
    motionThreshold = motion_threshold_sit;
  }

  float motion = readCurrentMotion();
  motionHigh = (motion > motionThreshold);

  if (motionHigh) {
    Serial.println("High motion -> vibration blocked");
  }

  bool looksBad  = (!motionHigh) && (dNorm + START_MARGIN < dIdeal);
  bool looksGood = (!motionHigh) && (dIdeal + STOP_MARGIN < dNorm);

  unsigned long now = millis();

  switch(state) {
    case GOOD:
      if (!motionHigh && looksBad) {
        state = BAD_PENDING;
        badStart = now;
        Serial.println("Bad posture -> BAD_PENDING (waiting 3s)");
      }
      break;

    case BAD_PENDING:
      if (motionHigh || looksGood) {
        state = GOOD;
      } else if (now - badStart >= BAD_HOLD_MS) {
        state = VIBRATING;
        setVibration(true);
        Serial.println("VIBRATION");
      }
      break;

    case VIBRATING:
      setVibration(false);
      state = COOLDOWN;
      cooldownStart = now;
      Serial.println("COOLDOWN started (7s)");
      break;

    case COOLDOWN:
      if (now - cooldownStart >= COOLDOWN_MS) {
        state = GOOD;
      }
      break;
  }

  // debug print
  Serial.print("family=");
  Serial.print(familyLabel(family));

  Serial.print(" | pitch=");
  Serial.print(pitch, 2);

  Serial.print(" roll=");
  Serial.print(roll, 2);

  Serial.print(" | motion=");
  Serial.print(motion, 2);
  Serial.print(motionHigh ? " (HIGH) " : " (low) ");

  Serial.print("| dStandN=");
  Serial.print(dStandN, 2);

  Serial.print(" dStandI=");
  Serial.print(dStandI, 2);

  Serial.print(" dSitN=");
  Serial.print(dSitN, 2);

  Serial.print(" dSitI=");
  Serial.print(dSitI, 2);

  Serial.print(" | chosen dIdeal=");
  Serial.print(dIdeal, 2);

  Serial.print(" dNorm=");
  Serial.print(dNorm, 2);

  Serial.print(" -> ");
  Serial.print(dIdeal < dNorm ? "IDEAL" : "NORMAL");

  Serial.print(" | state=");
  Serial.println(stateLabel(state));
}

void setup() {
  Serial.begin(115200);
  delay(1000);

  pinMode(VIB_PIN, OUTPUT);
  pinMode(LED_BUILTIN, OUTPUT);
  setVibration(false);

  if (!IMU.begin()) {
    Serial.println("IMU init failed. Check Arduino_LSM6DS3 + board selection.");
    while (1) {}
  }

  Serial.println("IMU OK");
  Serial.println("=== 1-Sensor Posture ML (Auto Stand/Sit + Normal/Ideal) ===");
  Serial.println("A = Stand Normal");
  Serial.println("B = Stand Ideal (+ motion threshold)");
  Serial.println("C = Sit Normal");
  Serial.println("D = Sit Ideal (+ motion threshold)");
  Serial.println("L = Live ON");
  Serial.println("S = Live OFF");
}

void loop() {
  if (Serial.available() > 0) {
    char c = Serial.read();

    if (c == 'A' || c == 'a') {
      Serial.println("Recording STANDING NORMAL 8s...");
      recordPrototype8s(P_stand_normal, "STANDING NORMAL", false, nullptr);
    }
    else if (c == 'B' || c == 'b') {
      Serial.println("Recording STANDING IDEAL 8s...");
      recordPrototype8s(P_stand_ideal, "STANDING IDEAL", true, &motion_threshold_stand);
    }
    else if (c == 'C' || c == 'c') {
      Serial.println("Recording SITTING NORMAL 8s...");
      recordPrototype8s(P_sit_normal, "SITTING NORMAL", false, nullptr);
    }
    else if (c == 'D' || c == 'd') {
      Serial.println("Recording SITTING IDEAL 8s...");
      recordPrototype8s(P_sit_ideal, "SITTING IDEAL", true, &motion_threshold_sit);
    }
    else if (c == 'L' || c == 'l') {
      Serial.println("LIVE ON");
      liveMode = true;
      state = GOOD;
      setVibration(false);
      cooldownStart = 0;
      currentFamily = FAMILY_STAND;
      candidateFamily = FAMILY_STAND;
      candidateCount = 0;
    }
    else if (c == 'S' || c == 's') {
      Serial.println("LIVE OFF");
      liveMode = false;
      setVibration(false);
      state = GOOD;
    }

    while (Serial.available()) Serial.read();
  }

  if (liveMode) {
    unsigned long now = millis();
    if (now - lastSample >= SAMPLE_MS) {
      lastSample = now;
      liveStepOnce();
    }
  }
}