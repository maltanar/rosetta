#ifndef WRAPPERREGDRIVER_H
#define WRAPPERREGDRIVER_H

#include <stdint.h>

// TODO wrapper driver should be a singleton
typedef unsigned int AccelReg;
typedef uint64_t AccelDblReg;

class WrapperRegDriver
{
public:
  virtual ~WrapperRegDriver() {}
  // (optional) functions for host-accelerator buffer management
  virtual void copyBufferHostToAccel(void * hostBuffer, void * accelBuffer, unsigned int numBytes) {}
  virtual void copyBufferAccelToHost(void * accelBuffer, void * hostBuffer, unsigned int numBytes) {}
  virtual void * allocAccelBuffer(unsigned int numBytes) {return 0;}
  virtual void deallocAccelBuffer(void * buffer) {}

  // (optional) functions for accelerator attach-detach handling
  virtual void attach(const char * name) {}
  virtual void detach() {}

  // (mandatory) register access methods for the platform wrapper
  virtual void writeReg32(unsigned int regOffs, AccelReg regValue) = 0;
  virtual AccelReg readReg32(unsigned int regOffs) = 0;
  
  virtual void writeReg64(unsigned int regOffs, AccelDblReg regValue) {
    writeReg32(regOffs, (AccelReg)(regValue & 0x00000000ffffffff));
    writeReg32(regOffs+4, (AccelReg)((regValue >> 32) & 0x00000000ffffffff));
  };
  
  virtual AccelDblReg readReg64(unsigned int regOffs) {
    AccelDblReg res = 0;
    res = readReg32(regOffs);
    res |= ((AccelDblReg) readReg32(regOffs+4)) << 32;
  };

};

#endif // WRAPPERREGDRIVER_H
