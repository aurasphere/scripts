/*
 * MIT License
 *
 * Copyright (c) 2016 Donato Rimenti
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
