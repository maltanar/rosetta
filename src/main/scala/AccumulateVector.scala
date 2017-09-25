package rosetta

import Chisel._

class TestAccumulateVector(vecElems: Int) extends RosettaAccelerator {
  val numMemPorts = 0
  val io = new RosettaAcceleratorIF(numMemPorts) {
    val vector_num_elems = UInt(OUTPUT, width = 32)
    val vector_in_addr = UInt(INPUT, width = log2Up(vecElems))
    val vector_in_data = UInt(INPUT, width = 32)
    val vector_in_write_enable = Bool(INPUT)
    val vector_sum_enable = Bool(INPUT)
    val vector_sum_done = Bool(OUTPUT)
    val result = UInt(OUTPUT, width = 32)
  }
  // instantiate the vector memory
  val memVec = Mem(UInt(width = 32), vecElems)
  // set up finite state machine for vector summation
  val sIdle :: sAccumulate :: sDone :: Nil = Enum(UInt(), 3)
  val regState = Reg(init = UInt(sIdle))
  // current vector index for accumulation
  val regVecInd = Reg(init = UInt(0, width = log2Up(vecElems)))
  // accumulator register
  val regVecAccumulator = Reg(init = UInt(0, width = 32))
  // drive the vector sum output from the sum register
  io.result := regVecAccumulator
  // drive result ready signal to low by default
  io.vector_sum_done := Bool(false)
  // drive number of vector elements from constant
  io.vector_num_elems := UInt(vecElems)
  // the signature can be e.g. used for checking that the accelerator has the
  // correct version. here the signature is regenerated from the current date.
  io.signature := makeDefaultSignature()

  switch(regState) {
      is(sIdle) {
        regVecAccumulator := UInt(0)
        regVecInd := UInt(0)
        when(io.vector_in_write_enable) {
          // enable writes to vector memory when write enable is high
          memVec(io.vector_in_addr) := io.vector_in_data
        }
        when(io.vector_sum_enable) {
          // go to accumulation state when sum is enabled
          regState := sAccumulate
        }
      }

      is(sAccumulate) {
        // accumulate vector at current index, increment index by one
        regVecAccumulator := regVecAccumulator + memVec(regVecInd)
        regVecInd := regVecInd + UInt(1)

        when(regVecInd === UInt(vecElems-1)) {
          // exit accumulation and go to done when all elements processed
          regState := sDone
        }
      }

      is(sDone) {
        // indicate that we are done, wait until sum enable is set to low
        io.vector_sum_done := Bool(true)
        when(!io.vector_sum_enable) {
          regState := sIdle
        }
      }
  }
}
