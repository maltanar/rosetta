SBT ?= sbt
SBT_FLAGS ?= -Dsbt.log.noformat=true
CC = g++
TOP ?= $(shell readlink -f .)
BUILD_DIR ?= $(TOP)/build
BUILD_DIR_VERILOG := $(BUILD_DIR)/hw/verilog
BUILD_DIR_HWCPP := $(BUILD_DIR)/hw/cpp_emu
BUILD_DIR_HWDRV := $(BUILD_DIR)/hw/driver
BUILD_DIR_EMULIB_CPP := $(BUILD_DIR)/hw/cpp_emulib
DRV_SRC_DIR := $(TOP)/src/main/cpp/regdriver
VIVADO_PROJ_SCRIPT := $(TOP)/src/main/script/make-vivado-project.tcl


HW_VERILOG := $(BUILD_DIR_VERILOG)/PYNQWrapper.v
BITFILE_PRJNAME := bitfile_synth
BITFILE_PRJDIR := $(BUILD_DIR)/bitfile_synth
FREQ_MHZ := 150.0

hw_verilog:
	$(SBT) $(SBT_FLAGS) "runMain rosetta.ChiselMain --backend v --targetDir $(BUILD_DIR_VERILOG)"

hw_cpp:
	$(SBT) $(SBT_FLAGS) "runMain rosetta.ChiselMain --backend c --targetDir $(BUILD_DIR_HWCPP)"

hw_driver:
	mkdir -p "$(BUILD_DIR_HWDRV)"; $(SBT) $(SBT_FLAGS) "runMain rosetta.DriverMain $(BUILD_DIR_HWDRV) $(DRV_SRC_DIR)"

hw_vivadoproj: hw_verilog
	vivado -mode batch -source $(VIVADO_PROJ_SCRIPT) -tclargs $(TOP) $(HW_VERILOG) $(BITFILE_PRJNAME) $(BITFILE_PRJDIR) $(FREQ_MHZ)



.PHONY: hw_verilog hw_cpp hw_driver hw_vivadoproj
