package rosetta

import Chisel._
import fpgatidbits.dma._
import fpgatidbits.streams._
import fpgatidbits.PlatformWrapper._

// Copy memory from a source to target.
class MemCpyExample() extends RosettaAccelerator {
  val numMemPorts = 2
  val io = new RosettaAcceleratorIF(numMemPorts) {
    val start = Bool(INPUT)
    val finished = Bool(OUTPUT)
    val srcAddr = UInt(INPUT, width = 64)
    val destAddr = UInt(INPUT, width = 64)
    val byteCount = UInt(INPUT, width = 32)
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

  // to read the data stream from DRAM, we'll use a component called StreamWriter
  // from fpgatidbits.dma:
  // https://github.com/maltanar/fpga-tidbits/blob/master/src/main/scala/fpgatidbits/dma/StreamWriter.scala
  // we'll start by describing the "static" (unchanging) properties of the data
  // stream
  val wdP = new StreamWriterParams(
    streamWidth = 32, /* read a stream of 32 bits */
    mem = PYNQZ1Params.toMemReqParams(),  /* PYNQ memory request parameters */
    maxBeats = 1, /* do not use bursts (set to e.g. 8 for better DRAM bandwidth)*/
    chanID = 0 /* stream ID for distinguishing between returned responses */
  )
  // now instantiate the StreamWriter with these parameters
  val writer = Module(new StreamWriter(wdP)).io

  // wire up the stream reader and writer to the parameters that will be
  // specified by the user at runtime
  // start signal
  reader.start := io.start
  reader.baseAddr := io.srcAddr    // pointer to start of the source data
  writer.start := io.start
  writer.baseAddr := io.destAddr    // pointer to start of the destination

  // number of bytes to read for both reader and writer
  // IMPORTANT: it's best to provide a byteCount which is divisible by
  // 64, as the fpgatidbits streaming DMA components have some limitations.
  reader.byteCount := io.byteCount
  writer.byteCount := io.byteCount

  // indicate when the transfer is finished.
  io.finished := writer.finished

  // push the read stream into the writer
  reader.out <> writer.in

  // wire up the read requests-responses against the memory port interface
  reader.req <> io.memPort(0).memRdReq
  io.memPort(0).memRdRsp <> reader.rsp

  // plug the unused write port
  io.memPort(0).memWrReq.valid := Bool(false)
  io.memPort(0).memWrDat.valid := Bool(false)
  io.memPort(0).memWrRsp.ready := Bool(false)

  // wire up the write requests-responses against the memory port interface
  writer.req <> io.memPort(1).memWrReq
  io.memPort(1).memWrRsp <> writer.rsp
  writer.wdat <> io.memPort(1).memWrDat

  // plug the unused read port
  plugMemReadPort(1)  // read port not used

  // instantiate a cycle counter for benchmarking
  val regCycleCount = Reg(init = UInt(0, 32))
  io.cycleCount := regCycleCount
  when(!io.start) {regCycleCount := UInt(0)}
  .elsewhen(io.start & !io.finished) {regCycleCount := regCycleCount + UInt(1)}

  io.led := io.btn

  // the signature can be e.g. used for checking that the accelerator has the
  // correct version. here the signature is regenerated from the current date.
  io.signature := makeDefaultSignature()
}
