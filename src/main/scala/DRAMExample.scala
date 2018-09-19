package rosetta

import Chisel._
import fpgatidbits.dma._
import fpgatidbits.streams._
import fpgatidbits.PlatformWrapper._

// read and sum a contiguous stream of 32-bit integers from PYNQ's DRAM
class DRAMExample() extends RosettaAccelerator {
  val numMemPorts = 1
  val io = new RosettaAcceleratorIF(numMemPorts) {
    val start = Bool(INPUT)
    val finished = Bool(OUTPUT)
    val baseAddr = UInt(INPUT, width = 64)
    val byteCount = UInt(INPUT, width = 32)
    val sum = UInt(OUTPUT, width = 32)
    val cycleCount = UInt(OUTPUT, width = 32)
  }
  // to read the data stream from DRAM, we'll use a component called StreamReader
  // from fpgatidbits.dma:
  // https://github.com/maltanar/fpga-tidbits/blob/master/src/main/scala/fpgatidbits/dma/StreamReader.scala
  // we'll start by describing the "static" (unchanging) properties of the data
  // stream
  val rdP = new StreamReaderParams(
    streamWidth = 32, /* read a stream of 32 bits */
    fifoElems = 8,    /* add a stream FIFO of 8 elements */
    mem = PYNQZ1Params.toMemReqParams(),  /* PYNQ memory request parameters */
    maxBeats = 1, /* do not use bursts (set to e.g. 8 for better DRAM bandwidth)*/
    chanID = 0, /* stream ID for distinguishing between returned responses */
    disableThrottle = true  /* disable throttling */
  )
  // now instantiate the StreamReader with these parameters
  val reader = Module(new StreamReader(rdP)).io
  // we'll use a StreamReducer to consume the data stream we get from DRAM:
  // https://github.com/maltanar/fpga-tidbits/blob/master/src/main/scala/fpgatidbits/streams/StreamReducer.scala
  val red = Module(new StreamReducer(
    32,     /* stream is 32-bit wide */
    0,      /* initial value for the reducer is 0 */
    {_+_}   /* use the + operator for reduction */
  )).io

  // wire up the stream reader and reducer to the parameters that will be
  // specified by the user at runtime
  // start signal
  reader.start := io.start
  red.start := io.start
  reader.baseAddr := io.baseAddr    // pointer to start of data
  // number of bytes to read for both reader and reducer
  // IMPORTANT: it's best to provide a byteCount which is divisible by
  // 64, as the fpgatidbits streaming DMA components have some limitations.
  reader.byteCount := io.byteCount
  red.byteCount := io.byteCount
  // indicate when the reduced is finished, and expose the reduction result (sum)
  io.sum := red.reduced
  io.finished := red.finished
  // wire up the read requests-responses against the memory port interface
  reader.req <> io.memPort(0).memRdReq
  io.memPort(0).memRdRsp <> reader.rsp
  // push the read stream into the reducer
  reader.out <> red.streamIn
  // plug the unused write port
  io.memPort(0).memWrReq.valid := Bool(false)
  io.memPort(0).memWrDat.valid := Bool(false)
  io.memPort(0).memWrRsp.ready := Bool(false)

  // instantiate a cycle counter for benchmarking
  val regCycleCount = Reg(init = UInt(0, 32))
  io.cycleCount := regCycleCount
  when(!io.start) {regCycleCount := UInt(0)}
  .elsewhen(io.start & !io.finished) {regCycleCount := regCycleCount + UInt(1)}

  // the signature can be e.g. used for checking that the accelerator has the
  // correct version. here the signature is regenerated from the current date.
  io.signature := makeDefaultSignature()
}
