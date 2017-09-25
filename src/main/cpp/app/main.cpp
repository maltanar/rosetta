#include <iostream>
using namespace std;
#include "platform.h"

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
  Run_TestAccumulateVector(platform);

  deinitPlatform(platform);

  return 0;
}
