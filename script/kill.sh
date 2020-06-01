#!/bin/sh
ps -e awwas | grep seed | awk '{print $2}' | xargs -I {} echo kill {} | sh
ps -e awwas | grep node | awk '{print $2}' | xargs -I {} echo kill {} | sh