package rosetta

import Chisel._

class Scale(array_size: Int, data_width: Int) extends RosettaAccelerator {
    def Mul(a: UInt, b: UInt):UInt = {
        a * b
    }

    val numMemPorts = 0
    val io = new RosettaAcceleratorIF(numMemPorts) {
        val nums_in = Vec.fill(array_size){UInt(INPUT, data_width)}

        // Need a good way to store weights
        val weights = Vec.fill(array_size){UInt(INPUT, data_width)}
        
        val data_out = Vec.fill(array_size){UInt(OUTPUT, data_width)}
    }

    for(i <- 0 to array_size-1){
        io.data_out(i) := Mul(io.nums_in(i), io.weights(i))
    }

}

class ScaleTests(c: Scale) extends Tester(c) {
    poke(c.io.nums_in, Array[BigInt](1,2,3,4))
    poke(c.io.weights, Array[BigInt](4,3,2,1))
    
    peek(c.io.data_out)
}
