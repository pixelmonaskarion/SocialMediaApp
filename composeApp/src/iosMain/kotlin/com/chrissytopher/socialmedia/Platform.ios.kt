package com.chrissytopher.socialmedia

import platform.UIKit.UIDevice

class IOSPlatform: Platform() {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

