#include <ap_int.h>

// Remember to set the top-level directive for using 64-bit AXI MM addresses
// e.g in the script.tcl file:
// config_interface -m_axi_addr64

unsigned int BlackBoxJam(ap_uint<64> * ptr, unsigned int count) {
#pragma HLS INTERFACE s_axilite port=return bundle=control
#pragma HLS INTERFACE s_axilite port=count bundle=control
#pragma HLS INTERFACE m_axi offset=slave port=ptr bundle=hostmem
#pragma HLS INTERFACE s_axilite port=ptr bundle=control
	unsigned int sum = 0;
	ap_uint<64> elem;

	for(unsigned int i = 0; i < count/2; i++) {
		elem = ptr[i];
		sum += (ap_uint<32>)(elem & 0xffffffff);
		sum += (ap_uint<32>)((elem >> 32) & 0xffffffff);
	}

	return sum;
}
