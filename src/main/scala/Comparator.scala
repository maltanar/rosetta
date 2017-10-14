package rosetta

import Chisel._

class Comparator(dataWidth: Int) extends RosettaAccelerator {
  val numMemPorts = 0
  val io = new RosettaAcceleratorIF(numMemPorts) {
    val in0 = UInt(INPUT, dataWidth)
    val in1 = UInt(INPUT, dataWidth)
    val output = UInt(OUTPUT, 1)
  }

  io.output := Mux(io.in0 >= io.in1, UInt(1), UInt(0))
  
}

class ComparatorTest(c: Comparator) extends Tester(c) {
  val in0 = 50
  val in1 = 5
  poke(c.io.in0, in0)
  poke(c.io.in1, in1)
  expect(c.io.output, 1)
  peek(c.io.output)
}


