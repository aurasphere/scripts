/**
 * Program that reads a char incoming from Bluetooth connection and turns ON and OFF a led.
 */

int input = 2;  // Bluetooth pin
int output = 3; // LED pin
String inputString = "";
char junk;

void setup() {
  Serial.begin(9600);
  pinMode(input, INPUT);
  pinMode(output, OUTPUT);
}

void loop() {
  if(Serial.available()){
  while(Serial.available())
    {
      char inChar = (char)Serial.read(); //read the input
      inputString += inChar;        //make a string of the characters coming on serial
    }
    Serial.println(inputString);
    while (Serial.available() > 0)  
    { junk = Serial.read() ; }      // clear the serial buffer
    if(inputString == "H"){         //in case of 'H' turn the LED on
      digitalWrite(output, HIGH);  
    }else if(inputString == "L"){   //in case of 'L' turn the LED off
      digitalWrite(output, LOW);
    }
    inputString = "";
  }
}
