///this is the file for the arduino that can communicate with the bluetooth devices like on our phone
///READ THE FOLLOWING REQUIREMENTS BELOW
///IMPORTANT!!!
///To flash the arduino properly, please make sure you have the following installed
/// libraries: WiFININA, ArduinoBLE, Arduino_LSM6DS3, Arduino_SpiNINA (make sure all are up to date)
/// boards: Arduino AVR Boards, Arduino Mbed OS Nano Boards, Arduino SAMD Boards
/// WifiNINA firmware 3.0 (this should already be flashed on the NANO)
///       if you still want to make the firmware is up to date go to...
///        File->Examples->WiFiNINA->Tools->FirmwareUpdater
///         then flash the nano to make sure the nano is u.t.d

//wiring diagram below
//nano -> motor controller
//3.3V to vin
//gnd -> gnd
//a4 -> sda
//a5 -> scl

//motor controller -> vibration coin
// - terminal -> blue wire
// + terminal -> red wire


#include <ArduinoBLE.h>
#include <Arduino_LSM6DS3.h>
#include <TimerFreeTone.h>
#include <Wire.h>
#include <Adafruit_DRV2605.h>

//custom ble service to read and display data thru the app
BLEService postureService("12345678-1234-1234-1234-1234567890ab");
BLEStringCharacteristic dataChar(
  "abcdefab-1234-1234-1234-abcdefabcdef", 
  BLERead | BLENotify,
  60
);
Adafruit_DRV2605 drv;

void setup() {
  Serial.begin(9600);
  delay(1500);

  if (!IMU.begin()) {
    Serial.println("imu failed");
    while (1);
  }

  if (!BLE.begin()) {
    Serial.println("bluetooth fail");
    while (1);
  }

  BLE.setLocalName("The Perfectionist");
  BLE.setAdvertisedService(postureService);

  postureService.addCharacteristic(dataChar);
  BLE.addService(postureService);

  dataChar.writeValue("boot");
  BLE.advertise();
  drv.begin();
  drv.selectLibrary(1);                
  drv.setMode(DRV2605_MODE_INTTRIG);   // internal trigger

  Serial.println("Advertising the Perfectionist");
}

void loop() {
  BLEDevice central = BLE.central();

  //this just makes sure the serial monitor prints the same vals. open it under 
  if (central) {
    Serial.print("connected to ");
    Serial.println(central.address());
    Serial.println("You are connected!");
    TimerFreeTone(2,260,2000); //Connected Buzzer Indicator

    while (central.connected()) {

    

      //acceleration and gyroscope vals
      float ax, ay, az;
      float gx, gy, gz;

      //this reads the accel and gyro data from the LSM library
      if (IMU.accelerationAvailable()) IMU.readAcceleration(ax, ay, az);
      if (IMU.gyroscopeAvailable())    IMU.readGyroscope(gx, gy, gz);

      //so the app so far will only determine pitch and roll.
      //quick reminder, pitch is x axis and roll is y axis. remember by thinking that wheels roll on its y axis or something
      float pitch = atan2(ax, sqrt(ay * ay + az * az)) * 180.0 / PI;
      float roll  = atan2(ay, sqrt(ax * ax + az * az)) * 180.0 / PI;

      //simple algorithm to determine good or bad posture
      const float thresh = 15.0; //discuss with team to make sure this is at a good val
      const char* state = (abs(pitch) > thresh || abs(roll) > thresh) ? "BAD" : "GOOD";
      if (state == "BAD")
      {
        drv.setWaveform(0, 1); // rising edge
        drv.setWaveform(1, 0); // falling edge
        TimerFreeTone(2,240,1000);
        drv.go();

        delay(1000);

      }

      //this is the csv payload for Android parsing. basically what allows the app to read the data
      String msg = String(roll, 1) + "," + String(pitch, 1) + "," + state;

      dataChar.writeValue(msg);

      //this it to debug the serial monitor. 
      Serial.println(msg);

      delay(100); // <- change this value to change the hz, on default its 10 hz
    }

    Serial.println("disconnected");
    TimerFreeTone(2,240,1500);
    TimerFreeTone(2,200,1500); //Disconnected Buzzer Indicator
  }
}