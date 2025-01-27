SUMMARY = "bpftrace"
HOMEPAGE = "https://github.com/iovisor/bpftrace"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS += "bison-native \
            flex-native \
            gzip-native \
            elfutils \
            bcc \
            systemtap \
            libcereal \
            libbpf \
            "

DEPENDS += "${@bb.utils.contains('PTEST_ENABLED', '1', 'gtest xxd-native', '', d)}"
PV .= "+git${SRCREV}"
RDEPENDS:${PN} += "bash python3 xz"
RDEPENDS:${PN}-ptest += "bash"

SRC_URI = "git://github.com/iovisor/bpftrace;branch=master;protocol=https \
           file://run-ptest \
"
SRCREV = "a277ec42102c463d656df8f64eb2f7e87e322210"

S = "${WORKDIR}/git"

inherit cmake ptest

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    install -m 755 ${B}/tests/bpftrace_test ${D}${PTEST_PATH}/tests
    cp -rf ${B}/tests/runtime* ${D}${PTEST_PATH}/tests
    cp -rf ${B}/tests/test* ${D}${PTEST_PATH}/tests
}

def llvm_major_version(d):
    pvsplit = d.getVar('LLVMVERSION').split('.')
    return pvsplit[0]

LLVM_MAJOR_VERSION = "${@llvm_major_version(d)}"

EXTRA_OECMAKE = " \
    -DCMAKE_ENABLE_EXPORTS=1 \
    -DCMAKE_BUILD_TYPE=Release \
    -DLLVM_REQUESTED_VERSION=${LLVM_MAJOR_VERSION} \
    -DENABLE_MAN=OFF \
"
EXTRA_OECMAKE += "${@bb.utils.contains('PTEST_ENABLED', '1', '-DBUILD_TESTING=ON', '-DBUILD_TESTING=OFF', d)}"

COMPATIBLE_HOST = "(x86_64.*|aarch64.*|powerpc64.*|riscv64.*)-linux"
COMPATIBLE_HOST:libc-musl = "null"
