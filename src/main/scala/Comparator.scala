package rosetta

import Chisel._

class Comparator extends RosettaAccelerator {
  val numMemPorts = 0
  val io = new RosettaAcceleratorIF(numMemPorts) {
    val in0 = UInt(INPUT, 1)
    val in1 = UInt(INPUT, 1)
    val output = UInt(OUTPUT, 1)
  }

  io.output := Mux(io.in0 >= io.in1, UInt(1), UInt(0))
  
}

class ComparatorTest(c: Comparator) extends Tester(c) {
  val in0 = 1
  val in1 = 0
  poke(c.io.in0, in0)
  poke(c.io.in1, in1)
  step(1)
  expect(c.io.output, 1)
}


