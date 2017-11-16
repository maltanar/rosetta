#include <iostream>
using namespace std;
#include "platform.h"

// this header file is generated during HLS synthesis,
// and contains the register map addresses for accelerator I/O
#include "xblackboxjam_hw.h"

int main(int argc, char **argv) {
  try {
    theDriver = initPlatform();

    unsigned int numToSum = 0;
    cout << "Enter number of integers to generate and sum: " << endl;
    cin >> numToSum;

    unsigned int bufSize = numToSum * sizeof(unsigned int);
    unsigned int * hostBuf = new unsigned int[numToSum];
    // set up the input buffer
    for(unsigned int i = 0; i < numToSum; i++) {
        hostBuf[i] = i+1;
    }
    cout << "Setting up the accelerator..." << endl;
    // copy buffer to accelerator
    void * accelBuf = theDriver->allocAccelBuffer(bufSize);
    theDriver->copyBufferHostToAccel(hostBuf, accelBuf, bufSize);
    // configure the accelerator register inputs
    theDriver->writeReg64(XBLACKBOXJAM_CONTROL_ADDR_PTR_V_DATA, (AccelDblReg) accelBuf);
    theDriver->writeReg32(XBLACKBOXJAM_CONTROL_ADDR_COUNT_DATA, numToSum);
    cout << "Executing..." << endl;
    // give the start signal
    theDriver->writeReg32(XBLACKBOXJAM_CONTROL_ADDR_AP_CTRL, 1);
    // poll accelerator's done signal
    while((theDriver->readReg32(XBLACKBOXJAM_CONTROL_ADDR_AP_CTRL) & 0x2) == 0);

    cout << "Result: " << theDriver->readReg32(XBLACKBOXJAM_CONTROL_ADDR_AP_RETURN) << endl;
    cout << "Expected: " << (numToSum*(numToSum+1))/2 << endl;

    deinitPlatform(theDriver);
  } catch(const char *e) {
    cout << "Error: " << e << endl;
  }

  return 0;
}

