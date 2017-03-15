package com.heqiying.fundmng.settle.utils

import java.io.File

import io.github.cloudify.scala.spdf._

import scala.sys.process._
import ParamShow._

class ImageGen(executablePath: String, config: ImageGenConfig) {
  validateExecutable_!(executablePath)

  /**
   * Runs the conversion tool to convert sourceDocument HTML into
   * destinationDocument Image.
   */
  def run[A, B](sourceDocument: A, destinationDocument: B)(implicit sourceDocumentLike: SourceDocumentLike[A], destinationDocumentLike: DestinationDocumentLike[B]): Int = {
    val commandLine = toCommandLine(sourceDocument, destinationDocument)
    val process = Process(commandLine)
    def source = sourceDocumentLike.sourceFrom(sourceDocument) _
    def sink = destinationDocumentLike.sinkTo(destinationDocument) _

    (sink compose source)(process).!
  }

  /**
   * Generates the command line needed to execute `wkhtmltoimage`
   */
  private def toCommandLine[A: SourceDocumentLike, B: DestinationDocumentLike](source: A, destination: B): Seq[String] =
    Seq(executablePath) ++
      ImageGenConfig.toParameters(config) ++
      Seq(
        "--quiet",
        implicitly[SourceDocumentLike[A]].commandParameter(source),
        implicitly[DestinationDocumentLike[B]].commandParameter(destination)
      )

  /**
   * Check whether the executable is actually executable, if it isn't
   * a NoExecutableException is thrown.
   */
  private def validateExecutable_!(executablePath: String): Unit = {
    val executableFile = new File(executablePath)
    if (!executableFile.canExecute) throw new NoExecutableException(executableFile.getAbsolutePath)
  }

}

object ImageGen {

  /**
   * Creates a new instance of image with default configuration
   * @return
   */
  def apply(config: ImageGenConfig): ImageGen = {
    val executablePath: String = ImageGenConfig.findExecutable.getOrElse {
      throw new NoExecutableException(System.getenv("PATH"))
    }

    apply(executablePath, config)
  }

  /**
   * Creates a new instance of image with the passed configuration
   */
  def apply(executablePath: String, config: ImageGenConfig): ImageGen =
    new ImageGen(executablePath, config)

}

/**
 * Holds the configuration parameters of wkhtmltoimage
 */
trait ImageGenConfig {

  /**
   * Options for `wkhtmltoimage` command
   * See `wkhtmltoimage --extended-help` for a description of each option
   */

  val allow = Parameter[Iterable[String]]("allow")

  val width = Parameter[Int]("width")

  val disableSmartWidth = Parameter[Boolean]("disable-smart-width")

  val encoding = Parameter[String]("encoding")
}

object ImageGenConfig {

  /**
   * An instance of the default configuration
   */
  object default extends ImageGenConfig

  /**
   * Generates a sequence of command line parameters from a `PdfKitConfig`
   */
  def toParameters(config: ImageGenConfig): Seq[String] = {
    import config._
    Seq(
      allow.toParameter,
      width.toParameter,
      disableSmartWidth.toParameter,
      encoding.toParameter
    ).flatten
  }

  /**
   * Attempts to find the `wkhtmltoimage` executable in the system path.
   * @return
   */
  def findExecutable: Option[String] = try {
    val os = System.getProperty("os.name").toLowerCase
    val cmd = if (os.contains("windows")) "where wkhtmltoimage" else "which wkhtmltoimage"

    Option(cmd.!!.trim).filter(_.nonEmpty)
  } catch {
    case _: RuntimeException => None
  }

}
