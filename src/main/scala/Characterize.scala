package rosetta

import sys.process._
import java.io.File
import Chisel._

// utilities for estimating FPGA resource and Fmax

// bundled up numbers/results from characteriation
class CharacterizeResult(
  val lut: Int,
  val reg: Int,
  val dsp: Int,
  val bram: Int,
  val target_ns: Double,
  val fmax_mhz: Double
) {
  def printSummary() = {
    println(s"$lut LUTs, $reg FFs, $bram BRAMs, $dsp DSP slices, $fmax_mhz MHz")
  }
}

object VivadoSynth {
  val path: String = "build/characterize"
  val args = Array[String]("--backend", "v", "--targetDir", path)

  def characterize(instFxn: () => Module): CharacterizeResult = {
    // call Chisel to generate Verilog
    chiselMain(args, instFxn)
    val topModuleName: String = instFxn().getClass.getSimpleName
    // run Nachiket Kapre's quick synthesis-and-characterization scripts
    val compile_res = Process(s"vivadocompile.sh $topModuleName clk", new File(path)).!!
    val result_res =  Process(s"vivadoresults.sh $topModuleName", new File(path)).!!
    // do some string parsing to pull out the numbers
    val result_lines = result_res.split("\n")
    val luts_fields = result_lines(0).split('|').map(_.trim)
    val regs_fields = result_lines(1).split('|').map(_.trim)
    val dsps_fields = result_lines(2).split('|').map(_.trim)
    val bram_fields = result_lines(3).split('|').map(_.trim)
    val slack_fields = result_lines(4).split(':').map(_.trim)
    val req_ns_fields = result_lines(5).split(':').map(_.trim)
    val slack_ns: Double = slack_fields(1).split("ns")(0).toDouble
    val req_ns: Double = req_ns_fields(1).split("ns")(0).toDouble
    val fmax_mhz: Double = 1000.0 / (req_ns - slack_ns)

    return new CharacterizeResult(
      lut = luts_fields(2).toInt, reg = regs_fields(2).toInt,
      bram = bram_fields(2).toInt,
      dsp = dsps_fields(2).toInt, target_ns = req_ns, fmax_mhz = fmax_mhz
    )
  }
}
