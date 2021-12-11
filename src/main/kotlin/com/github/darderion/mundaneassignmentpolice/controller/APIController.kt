package com.github.darderion.mundaneassignmentpolice.controller

import com.github.darderion.mundaneassignmentpolice.checker.Checker
import com.github.darderion.mundaneassignmentpolice.pdfdocument.Annotations
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Section
import com.github.darderion.mundaneassignmentpolice.utils.FileUploadUtil
import com.github.darderion.mundaneassignmentpolice.wrapper.PDFBox
import mu.KotlinLogging
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.view.RedirectView
import java.io.File
import kotlin.math.abs
import kotlin.random.Random

const val pdfFolder = "build/"

@RestController
class APIController {
	val pdfBox = PDFBox()

	@GetMapping("/api/viewPDFText")
	fun getPDFText(@RequestParam pdfName: String) =
		pdfBox.getText("$pdfFolder$pdfName").also { logger.info("ViewPDF(pdfName = $pdfName)") }

	@GetMapping("/api/viewPDFSections")
	fun getPDFSections(@RequestParam pdfName: String): List<Section> {
		val pdf = pdfBox.getPDF("$pdfFolder$pdfName").also { logger.info("ViewPDFSections(pdfName = $pdfName)") }
		return if (pdf.areas != null) pdf.areas.sections else listOf()
	}

	@GetMapping("/api/viewPDFLines")
	fun getPDFLines(@RequestParam pdfName: String) =
		pdfBox.getLines("$pdfFolder$pdfName").also { logger.info("ViewPDFLines(pdfName = $pdfName)") }

	@GetMapping("/api/viewRuleViolations")
	fun getRulesViolations(@RequestParam pdfName: String) =
		Checker().getRuleViolations("$pdfFolder$pdfName").also { logger.info("ViewRuleViolations(pdfName = $pdfName)") }

	@GetMapping("/api/viewPDFImages")
	fun getPDFImages(@RequestParam pdfName: String) =
		pdfBox.getImages("$pdfFolder$pdfName").toList().also { logger.info("ViewPDFImages(pdfName = $pdfName)") }

	@PostMapping("/api/uploadPDF")
	fun uploadPDF(@RequestParam("pdf") multipartFile: MultipartFile): RedirectView? {
		if (multipartFile.originalFilename == null) {
			return null
		}
		// val fileName = StringUtils.cleanPath(multipartFile.originalFilename!!)
		val fileName = (abs(Random.nextInt() % 10000)).toString()

		FileUploadUtil.removeRandomFile(pdfFolder, 10)

		FileUploadUtil.saveFile(pdfFolder, fileName, multipartFile)
		logger.info("UploadPDF(pdfName = $fileName)")
		return RedirectView("/#/viewPDF?pdfName=$fileName&numPages=${pdfBox.getPDFSize("$pdfFolder$fileName")}", true)
	}

	@GetMapping("/api/viewPDF.pdf")
	@ResponseBody
	fun getPDF(@RequestParam("pdfName") fileName: String) = File("$pdfFolder$fileName").readBytes()

	@GetMapping("/api/viewPDFRuleViolations.pdf")
	@ResponseBody
	fun getPDF(@RequestParam("pdfName") fileName: String,
			   @RequestParam("page") page: Int?,
			   @RequestParam("line") line: Int?
	): ByteArray {
		val directory = "${pdfFolder}ruleviolations/"
		FileUploadUtil.removeRandomFile(directory, 10)

		val pdf = PDFBox().getPDF("$pdfFolder$fileName")
		val pdf2 = Annotations.underline(pdf,
			if (page == null || line == null) {
				Checker().getRuleViolations(fileName).map { it.lines }.flatten()
			} else
				listOf(pdf.text.first { it.page == page && it.line == line })
		)
		logger.info("File created: $pdf2")

		return File("${pdfFolder}ruleviolations/${fileName.replace(".pdf", "")}$line-${page}.pdf").readBytes()
	}

	@GetMapping("/api/getPDFSize")
	fun getPDFSize(@RequestParam pdfName: String) = pdfBox.getPDFSize("$pdfFolder$pdfName")

	private companion object {
		private val logger = KotlinLogging.logger {}
	}
}
