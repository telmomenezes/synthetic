#!/usr/bin/Rscript

library(ggplot2)

args <- commandArgs(TRUE)
pdf(file=args[2])
evcdata <- read.csv(args[1])
d <- ggplot(evcdata, aes(log(evc_in), log(evc_out))) + stat_bin2d(aes(fill=log1p(..count..)))
print(d)
