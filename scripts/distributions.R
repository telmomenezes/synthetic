library(plyr)
library(ggplot2)
library(scales)
library(gridExtra)


args <- commandArgs(TRUE)

dir <- args[1]
pdfOut <- args[2]

directed <- T
if (length(args) > 2) {
  directed <- F
}

plot3 <- function(distr, xlabel, logX, logY, bars, legend) {
  dataSynt<-read.table(file=paste(dir, "/", distr, ".csv", sep=""), header=T, sep=",")
  dataSynt$type <- "synthetic"

  dataReal<-read.table(file=paste(dir, "/targ_", distr, ".csv", sep=""), header=T, sep=",")
  dataReal$type <- "real"

  dataRandom<-read.table(file=paste(dir, "/random_", distr, ".csv", sep=""), header=T, sep=",")
  dataRandom$type <- "random"

  data <- rbind(dataRandom, dataSynt, dataReal)

  if (logY && bars) {
    data$freq <- ifelse(data$freq < 1, 0.000001, data$freq)
  }

  minX <- min(data$value)
  maxX <- max(data$value)
  minY <- min(data$freq)
  maxY <- max(data$freq)

  cdata <- ddply(data, .(value, type), summarise, 
               N    = length(freq),
               mean = mean(freq),
               sd   = sd(freq),
               se   = sd(freq) / sqrt(length(freq)),
               min  = min(freq),
               max  = max(freq) ) 
  
  if (bars) {
    cdata$value <- factor(cdata$value)
    p <- ggplot(cdata, aes(x=value, y=mean, fill=type))
    p <- p + geom_bar(position=position_dodge(), stat="identity")
    p <- p + geom_errorbar(aes(ymin=mean-se, ymax=mean+se), size=.3, width=.07, position=position_dodge(.9))
    p <- p + scale_fill_discrete(limits=c("real", "synthetic", "random"))
  }
  else {
    p <- ggplot(cdata, aes(x=value, y=mean, colour=type))
    p <- p + geom_point(alpha=0.8)
    p <- p + geom_errorbar(aes(ymin=mean-se, ymax=mean+se), size=.3, width=.2, alpha=0.6)
    p <- p + scale_colour_discrete(limits=c("real", "synthetic", "random"))
  }

  if (!legend) {
    p <- p + opts(legend.position="none")
  }

  p <- p + xlab(xlabel)
  p <- p + ylab("")

  p <- p + opts(axis.line = theme_segment(colour = "black"),
    panel.grid.major = theme_blank(),
    panel.grid.minor = theme_blank(),
    panel.border = theme_blank(),
    panel.background = theme_blank()) 

  if (logX) {
    if (minX == 0) {
      minX <- 1
    }
    p <- p + scale_x_log10(limits=c(minX, maxX))
  }
  else {
    if (!bars) {
      p <- p + scale_x_continuous(limits=c(minX, maxX))
    }
  }

  if (logY) {
    if (minY == 0) {
      minY <- 1
    }
    if (bars) {
      p <- p + scale_y_log10(limits=c(1, maxY))
    }
    else {
      p <- p + scale_y_log10(limits=c(minY, maxY))
    }
  }
  else {
    if (!bars) {
      p <- p + scale_y_continuous(limits=c(minY, maxY))
    }
  }

  p
}

theme(plot.margin = unit(c(0, 0, 0, 0), "cm"))

if (directed) {
  p1 <- plot3("in_degrees", "in degree", T, T, F, F)
  p2 <- plot3("out_degrees", "out degree", T, T, F, F)
  p3 <- plot3("d_pagerank", "directed PageRank", F, T, F, F)
  p4 <- plot3("u_pagerank", "undirected PageRank", F, T, F, F)
  p5 <- plot3("d_dists", "directed distance", F, T, T, F)
  p6 <- plot3("u_dists", "undirected distance", F, T, T, F)
  p7 <- plot3("triadic_profile", "motif", F, T, T, T)

  first6 = arrangeGrob(p1, p2, p3, p4, p5, p6)

  pdf(file=pdfOut, height=8, width=6)
  grid.arrange(first6, p7, heights=c(3/4,1/4), nrow=2)
} else {
  p1 <- plot3("degrees", "degree", T, T, F, F)
  p2 <- plot3("u_pagerank", "PageRank", F, T, F, F)
  p3 <- plot3("u_dists", "distance", F, T, T, F)
  p4 <- plot3("triadic_profile", "motif", F, T, T, T)

  plots = arrangeGrob(p1, p2, p3, p4)

  pdf(file=pdfOut, height=4, width=6)
  grid.arrange(plots, heights=c(1), nrow=1)
}
