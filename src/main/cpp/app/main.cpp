#include <iostream>
using namespace std;
#include "platform.h"

// uncomment this block for the DRAMExample
#include "DRAMExample.hpp"
void Run_DRAMExample(WrapperRegDriver * platform) {
  DRAMExample t(platform);

  cout << "Signature: " << hex << t.get_signature() << dec << endl;
  unsigned int ub = 0;
  // why divisible by 16? fpgatidbits DMA components may not work if the
  // number of bytes is not divisible by 64. since we are using 4-byte words,
  // 16*4=64 ensures divisibility.
  cout << "Enter upper bound of sum sequence, divisible by 16: " << endl;
  cin >> ub;
  if(ub % 16 != 0) {
    cout << "Error: Upper bound must be divisible by 16" << endl;
    return;
  }

  unsigned int * hostBuf = new unsigned int[ub];
  unsigned int bufsize = ub * sizeof(unsigned int);
  unsigned int golden = (ub*(ub+1))/2;

  for(unsigned int i = 0; i < ub; i++) { hostBuf[i] = i+1; }

  void * accelBuf = platform->allocAccelBuffer(bufsize);
  platform->copyBufferHostToAccel(hostBuf, accelBuf, bufsize);

  t.set_baseAddr((AccelDblReg) accelBuf);
  t.set_byteCount(bufsize);

  t.set_start(1);

  while(t.get_finished() != 1);

  platform->deallocAccelBuffer(accelBuf);
  delete [] hostBuf;

  AccelReg res = t.get_sum();
  cout << "Result = " << res << " expected " << golden << endl;
  unsigned int cc = t.get_cycleCount();
  cout << "#cycles = " << cc << " cycles per word = " << (float)cc/(float)ub << endl;
  t.set_start(0);
}

/*
// uncomment this block for the BRAMExample
#include "BRAMExample.hpp"
void Run_BRAMExample(WrapperRegDriver * platform) {
  BRAMExample t(platform);

  cout << "Signature: " << hex << t.get_signature() << dec << endl;
  while(1) {
    char cmd;
    cout << "Commands: (w)rite, (r)ead, (q)uit" << endl;
    cin >> cmd;
    switch (cmd) {
      case 'q':
        return;
      case 'w':
        unsigned int waddr, wdata;
        cout << "Enter write address: " << endl;
        cin >> waddr;
        t.set_write_addr(waddr);
        cout << "Enter write data: " << endl;
        cin >> wdata;
        t.set_write_data(wdata);
        // pulse the write enable to complete the write
        t.set_write_enable(1);
        t.set_write_enable(0);
        break;
      case 'r':
        unsigned int raddr;
        cout << "Enter read address: " << endl;
        cin >> raddr;
        t.set_read_addr(raddr);
        cout << "Read data: " << t.get_read_data();
        break;
      default:
        cout << "Unrecognized command" << endl;
    }
  }
}
*/

/*
// uncomment this block for the TestAccumulateVector example
#include "TestAccumulateVector.hpp"
void Run_TestAccumulateVector(WrapperRegDriver * platform) {
  TestAccumulateVector t(platform);

  cout << "Signature: " << hex << t.get_signature() << dec << endl;
  unsigned int num_vec_elems = t.get_vector_num_elems();
  cout << "Enter " << num_vec_elems << " elements: " << endl;
  unsigned int expected_acc = 0;
  for(unsigned int i = 0; i < num_vec_elems; i++) {
    unsigned int elem = 0;
    cin >> elem;
    expected_acc += elem;
    t.set_vector_in_addr(i);
    t.set_vector_in_data(elem);
    t.set_vector_in_write_enable(1);
    t.set_vector_in_write_enable(0);
  }

  // enable accumulation, wait until done
  t.set_vector_sum_enable(1);
  while(t.get_vector_sum_done() != 1);
  unsigned int result = t.get_result();

  // go back to idle, will clear result
  t.set_vector_sum_enable(0);

  cout << "Result: " << result << " expected: " << expected_acc << endl;
}
*/

/*
// uncomment this block for the TestRegOps example
#include "TestRegOps.hpp"
bool Run_TestRegOps(WrapperRegDriver * platform) {
  TestRegOps t(platform);

  cout << "Signature: " << hex << t.get_signature() << dec << endl;
  cout << "Enter two operands to sum: ";
  unsigned int a, b;
  cin >> a >> b;

  t.set_op_0(a);
  t.set_op_1(b);

  cout << "Result: " << t.get_sum() << " expected: " << a+b << endl;

  return (a+b) == t.get_sum();
}
*/

int main()
{
  WrapperRegDriver * platform = initPlatform();

  //Run_TestRegOps(platform);
  //Run_TestAccumulateVector(platform);
  //Run_BRAMExample(platform);
  Run_DRAMExample(platform);

  deinitPlatform(platform);

  return 0;
}
