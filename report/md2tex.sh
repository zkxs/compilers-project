#!/bin/bash
pandoc -t latex ../grammar/$1 | sed -e 's/longtable/longtabu/' -e 's/\\begin{longtabu}\(.*\)$/\\begin{longtabu}\1 to \\textwidth {|X|X|X|}/' > tmp_$1.tex
