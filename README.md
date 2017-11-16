# Rosetta
Rosetta is a project template to rapidly deploy Vivado HLS accelerators on the Xilinx PYNQ platform. It uses the PlatformWrapper components from the fpga-tidbits framework for easy memory mapped register file management.

## Requirements
1. Xilinx Vivado 2016.4 and Vivado HLS (make sure vivado and vivado_hls are in PATH)
2. A PYNQ board with network access

## Quickstart
1. Clone this repository and cd into it
2. Run make pynq, this may take several minutes. This will create a deployment folder under build/rosetta
3. Run make rsync to copy generated files to the PYNQ board. You may have to edit the BOARD_URI variable in the Makefile to get this working.
4. Open a PYNQ terminal via ssh, and cd into ~/rosetta
5. Run sudo ./load_bitfile.sh to configure the FPGA with the bitfile
6. Run ./compile_sw.sh to compile the sample application
7. Run the sample application with sudo ./app
8. Enter e.g 16 -- you should see the sum of integers up to 16 (136) displayed as computed by the accelerator

## Under the Hood
1. Have a look at the HLS hardware description under the src/main/cpp/hls -- the pragmas used in the main function are important to remain compatible with the fixed interface.
2. Have a look at the example application under src/main/cpp/app -- note that it uses the auto-generated register driver to access the hardware signals. The register driver will be generated in build/hw/driver
3. Have a look at what the different Makefile targets generate inside the build/ folder. You can also try launching Vivado with the make launch_vivado_gui target after the project has been generated.
