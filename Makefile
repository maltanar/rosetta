# target frequency for Vivado FPGA synthesis
FREQ_MHZ := 100.0
PERIOD_NS := 10
# controls whether Vivado will run in command-line or GUI mode
VIVADO_MODE := batch # gui
# which C++ compiler to use
CC = g++
# scp/rsync target to copy files to board
BOARD_URI := xilinx@pynq:~/rosetta

# other project settings
SBT ?= sbt
SBT_FLAGS ?= -Dsbt.log.noformat=true
# internal build dirs and names for the Makefile
TOP ?= $(shell readlink -f .)
HLS_SRC_DIR := $(TOP)/src/main/hls
BUILD_DIR ?= $(TOP)/build
BUILD_DIR_PYNQ := $(BUILD_DIR)/rosetta
BUILD_DIR_HWDRV := $(BUILD_DIR)/hw/driver
HLS_PROJ := hls
HLS_IP_DIR := $(BUILD_DIR)/hls/sol1/impl/ip
HLS_DRV := $(HLS_IP_DIR)/drivers/BlackBoxJam_v1_0/src/xblackboxjam_hw.h
DRV_SRC_DIR := $(TOP)/src/main/cpp/regdriver
APP_SRC_DIR := $(TOP)/src/main/cpp/app
VIVADO_PROJ_SCRIPT := $(TOP)/src/main/script/host/make-vivado-project.tcl
VIVADO_SYNTH_SCRIPT := $(TOP)/src/main/script/host/synth-vivado-project.tcl
HLS_SYNTH_SCRIPT := $(TOP)/src/main/script/host/hls-syn.tcl
PYNQ_SCRIPT_DIR := $(TOP)/src/main/script/pynq
BITFILE_PRJNAME := bitfile_synth
BITFILE_PRJDIR := $(BUILD_DIR)/bitfile_synth
GEN_BITFILE_PATH := $(BITFILE_PRJDIR)/$(BITFILE_PRJNAME).runs/impl_1/procsys_wrapper.bit

# note that all targets are phony targets, no proper dependency tracking
.PHONY: hls hw_vivadoproj bitfile pynq_hw pynq_sw pynq rsync

$(BUILD_DIR):
	mkdir -p $(BUILD_DIR)

# run Vivado HLS synthesis
hls: $(BUILD_DIR)
	cd $(BUILD_DIR); vivado_hls -f $(HLS_SYNTH_SCRIPT) -tclargs $(HLS_PROJ) $(HLS_SRC_DIR) $(PERIOD_NS)

# set up register driver sources
hw_driver:
	mkdir -p "$(BUILD_DIR_HWDRV)"; cp $(HLS_DRV) $(BUILD_DIR_HWDRV); cp $(DRV_SRC_DIR)/* $(BUILD_DIR_HWDRV)/

# create a new Vivado project
hw_vivadoproj: hls
	vivado -mode $(VIVADO_MODE) -source $(VIVADO_PROJ_SCRIPT) -tclargs $(TOP) $(HLS_IP_DIR) $(BITFILE_PRJNAME) $(BITFILE_PRJDIR) $(FREQ_MHZ)

# launch Vivado in GUI mode with created project
launch_vivado_gui: 
	vivado -mode gui $(BITFILE_PRJDIR)/$(BITFILE_PRJNAME).xpr

# run bitfile synthesis for the Vivado project
bitfile: hw_vivadoproj
	vivado -mode $(VIVADO_MODE) -source $(VIVADO_SYNTH_SCRIPT) -tclargs $(BITFILE_PRJDIR)/$(BITFILE_PRJNAME).xpr

# copy bitfile to the deployment folder, make an empty tcl script for bitfile loader
pynq_hw: bitfile
	mkdir -p $(BUILD_DIR_PYNQ); cp $(GEN_BITFILE_PATH) $(BUILD_DIR_PYNQ)/rosetta.bit; touch $(BUILD_DIR_PYNQ)/rosetta.tcl

# copy all user sources and driver sources to the deployment folder
pynq_sw: hw_driver
	mkdir -p $(BUILD_DIR_PYNQ); cp $(BUILD_DIR_HWDRV)/* $(BUILD_DIR_PYNQ)/; cp -r $(APP_SRC_DIR)/* $(BUILD_DIR_PYNQ)/

# copy scripts to the deployment folder
pynq_script:
	cp $(PYNQ_SCRIPT_DIR)/* $(BUILD_DIR_PYNQ)/

# get everything ready to copy onto the PYNQ
pynq: pynq_hw pynq_sw pynq_script

# use rsync to synchronize contents of the deployment folder onto the PYNQ
rsync:
	rsync -avz $(BUILD_DIR_PYNQ) $(BOARD_URI)

# remove everything that is built
clean:
	rm -rf $(BUILD_DIR)
