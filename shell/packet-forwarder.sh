#!/bin/bash

# ============================================================================== #
# MIT License                                                                    #
#                                                                                #
# Copyright (c) 2017 Donato Rimenti                                              #
#                                                                                #
# Permission is hereby granted, free of charge, to any person obtaining a copy   #
# of this software and associated documentation files (the "Software"), to deal  #
# in the Software without restriction, including without limitation the rights   #
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell      #
# copies of the Software, and to permit persons to whom the Software is          #
# furnished to do so, subject to the following conditions:                       #
#                                                                                #
# The above copyright notice and this permission notice shall be included in     #
# all copies or substantial portions of the Software.                            #
#                                                                                #
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR     #
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,       #
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE    #
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER         #
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,  #
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE  #
# SOFTWARE.                                                                      #
# ============================================================================== #
#                                                                                #
# DESCRIPTION : Simple program that forwards a packet to another node.           #
# AUTHOR : Donato Rimenti                                                        #
# COPYRIGHT : Copyright (c) 2017 Donato Rimenti                                  #
# LICENSE : MIT                                                                  #
#                                                                                #
# ============================================================================== #

# Variables.
bindingPort=9876
bindingIp=$(hostname -I)
targetIp=
targetPort=
logFile=

# NOT YET TESTED
# Waits for an incoming packet, logs it and forwards to the target.
while true
  do
    packet=nc -k -l "$bindingPort"
    logger -f /var/log/packet-forwarder.log "[$(date +"%Y-%m-%d %H:%M:%S,%3N)] Forwarding [$packet] from [$bindingIp:$bindingPort] to [$targetIp:$targetPort]"
    echo "$packet" > nc -s "$bindingIp" -p "$bindingPort" "$targetIp" "$targetPort"
  done
