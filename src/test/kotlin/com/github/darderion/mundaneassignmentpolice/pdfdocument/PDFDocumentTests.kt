package com.github.darderion.mundaneassignmentpolice.pdfdocument

import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea.*
import com.github.darderion.mundaneassignmentpolice.pdfdocument.list.PDFList
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Section
import com.github.darderion.mundaneassignmentpolice.wrapper.PDFBox
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class PDFDocumentTests: StringSpec({
	"PDFDocument should contain TITLE_PAGE's lines" {
		PDFDocument("pdf.pdf", lines).text.any { it.area == TITLE_PAGE } shouldBe true
	}
	"PDFDocument should contain PAGE_INDEX's lines" {
		PDFDocument("pdf.pdf", lines).text.any { it.area == PAGE_INDEX } shouldBe true
	}
	"PDFDocument should contain TABLE_OF_CONTENT's lines" {
		PDFDocument("pdf.pdf", lines).text.any { it.area == TABLE_OF_CONTENT } shouldBe true
	}
	"PDFDocument's areas should not be initialized if PDF doesn't contain TABLE_OF_CONTENT" {
		PDFBox().getPDF("src/test/pdf.pdf").areas shouldBe null
	}
	"PDFDocument should contain SECTION's lines" {
		PDFDocument("pdf.pdf", lines).text.any { it.area == SECTION } shouldBe true
	}
	"PDFDocument should contain FOOTNOTE's lines" {
		PDFDocument("pdf.pdf", lines).text.any { it.area == FOOTNOTE } shouldBe true
	}
	"PDFDocument should contain BIBLIOGRAPHY's lines" {
		PDFDocument("pdf.pdf", lines).text.any { it.area == BIBLIOGRAPHY } shouldBe true
	}
	"PDFDocument should contain sections with section's title, section's index and section's content index" {
		val document = PDFDocument("pdf.pdf", lines)

		val sections = document.areas!!.sections

		sections.count() shouldBe contentSections.count()

		sections.forEachIndexed { index, section ->
			section shouldBe sections[index]
		}
	}
	"PDFDocument should contain PDFList with sections' titles" {
		val document = PDFDocument("pdf.pdf", lines)

		val sections = document.areas!!.tableOfContents

		val sectionsTitles = contentSections.map { it.title.split('.').last().dropWhile { it == ' ' } }

		val pdfList = PDFList("TABLE_OF_CONTENTS", mutableListOf(
			PDFList(sectionsTitles[0]),
			PDFList(sectionsTitles[1]),
			PDFList(sectionsTitles[2]),
			PDFList(sectionsTitles[3]),
			PDFList(sectionsTitles[4]),
			PDFList(sectionsTitles[5]),
			PDFList(sectionsTitles[6], mutableListOf(
				PDFList(sectionsTitles[7])
			)),
			PDFList(sectionsTitles[8], mutableListOf(
				PDFList(sectionsTitles[9]),
				PDFList(sectionsTitles[10])
			)),
			PDFList(sectionsTitles[11], mutableListOf(
				PDFList(sectionsTitles[12], mutableListOf(
					PDFList(sectionsTitles[13])
				)),
				PDFList(sectionsTitles[14])
			)),
			PDFList(sectionsTitles[15], mutableListOf(
				PDFList(sectionsTitles[16], mutableListOf(
					PDFList(sectionsTitles[17]),
					PDFList(sectionsTitles[18])
				)),
				PDFList(sectionsTitles[19])
			)),
			PDFList(sectionsTitles[20])
		))

		document.areas!!.tableOfContents shouldBe pdfList
	}
}) {
	private companion object {
		private val lines = PDFBox().getLines("src/test/cw1.pdf")

		private val contentSections = listOf(
			Section("Introduction",													55),
			Section("1. Incorrect type of «dash» symbol",								66),
			Section("2. Incorrect litlink",											81),
			Section("3. Incorrect list’s size",										91),
			Section("4. Footnote example",												112),
			Section("5. Ligature",														119),
			Section("6. Section with one subsection",									125),
			Section("6.1. Subsection Lorem",											127),
			Section("7. Section with two subsections",									132),
			Section("7.1. Subsection Lorem1",											134),
			Section("7.2. Subsection Lorem2",											136),
			Section("8. Section with two subsections but a single ’subsubsection’",	141, 143),
			Section("8.1. Subsection Lorem1",											144),
			Section("8.1.1. Lorem1–1",													149),
			Section("8.2. Subsection Lorem2",											156),
			Section("9. Section with two subsections and two ’subsubsections’",		162),
			Section("9.1. Subsection Lorem1",											165),
			Section("9.1.1. Lorem1–1",													170),
			Section("9.1.2. Lorem1–2",													177),
			Section("9.2. Subsection Lorem2",											184),
			Section("Заключение",														190)
		)
	}
}
