/**
 * Program which receives a TV command via Bluetooth and sends it
 * to the TV via infrared.
 */

#include <IRremote.h>

IRsend irsend; // Object which handles infrared transmissions
int irOutput = 3; // Infrared output pin
int input = 2;  // Bluetooth pin
int ledOutput = 4; // Check LED pin (not compulsory)
char junk;
String inputCommand;

// TV code commands.
unsigned long shutdown = 0x2FD48B7;
unsigned long channelUp = 0x2FDD827;
unsigned long channelDown = 0x2FDF807;
unsigned long volumeUp = 0x2FD58A7;
unsigned long volumeDown = 0x2FD7887;

void setup(){
  Serial.begin(9600);
  pinMode(input, INPUT);
  pinMode(irOutput, OUTPUT);
  pinMode(ledOutput, OUTPUT);
}

void loop() {
  if(Serial.available()){
     while(Serial.available())
    {
      char inChar = (char)Serial.read(); //read the input
      inputCommand += inChar;        //make a string of the characters coming on serial
    }
      unsigned long commandToSend;
      digitalWrite(ledOutput, HIGH); // turns on the LED
       Serial.println(inputCommand);
        if(inputCommand == "1")
          commandToSend = shutdown;
        else if (inputCommand == "2")
          commandToSend = channelUp;
        else if (inputCommand == "3")
          commandToSend = channelDown;
        else if (inputCommand == "4")
          commandToSend = volumeUp;
        else if (inputCommand == "5")
          commandToSend = volumeDown;
        else {
          Serial.print("Unrecognized command ");
          Serial.println(inputCommand);
          Serial.println("Returning...");
          return;
        }
      // Some logging.
      Serial.print("Option choose: ");
      Serial.println(inputCommand);
      Serial.print("Sending command: ");
      Serial.println(commandToSend, HEX); 
    while (Serial.available() > 0)  
    { junk = Serial.read(); }      // clear the serial buffer
    irsend.sendNEC(commandToSend, 32); // sends the command received to the TV.
  }
  digitalWrite(ledOutput, LOW);
  inputCommand = "";
}
