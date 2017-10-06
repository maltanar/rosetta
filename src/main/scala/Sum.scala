package rosetta

import Chisel._

class Sum(nums_in: Int, dataWidth: Int) extends RosettaAccelerator {
  def Adder(a: UInt, b:UInt):UInt = {
    a + b
  }

  val numMemPorts = 0
  val io = new RosettaAcceleratorIF(numMemPorts) {
    val nums = Vec.fill(nums_in){UInt(INPUT, dataWidth)}

    val data_out = UInt(OUTPUT, dataWidth)
  }

  io.data_out := io.nums.reduceLeft(Adder)
}

class SumTests(c: Sum) extends Tester(c) {
  var input = Array[BigInt](1,2,3,4,5,6,7,8,9)
  poke(c.io.nums, input)
  peek(c.io.data_out)
  expect(c.io.data_out, input.sum)
}
