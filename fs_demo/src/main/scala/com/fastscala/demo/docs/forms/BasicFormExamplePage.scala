package com.fastscala.demo.docs.forms

import com.fastscala.core.FSContext
import com.fastscala.demo.docs.MultipleCodeExamples2Page
import com.fastscala.demo.docs.data.{ CountriesData, Country }
import com.fastscala.js.Js
import com.fastscala.templates.bootstrap5.modals.BSModal5
import com.fastscala.templates.bootstrap5.utils.BSBtn
import com.fastscala.templates.form7.{ DefaultForm7, F7Field }
import com.fastscala.templates.form7.fields.F7SubmitButtonField
import com.fastscala.templates.form7.fields.layout.{ F7ContainerField, F7VerticalField }
import com.fastscala.templates.form7.fields.select.F7SelectField
import com.fastscala.templates.form7.fields.text.{ F7IntOptField, F7LocalDateOptField, F7StringField }
import com.fastscala.xml.scala_xml.FSScalaXmlEnv.given

class User1(
  var firstName: String,
  var lastName: String,
  var email: String,
  var phoneNumber: String,
  var securityLevel: Int,
  var countryOfResidence: Country,
  var birthDay: Option[String],
  var province: Province,
  var city: City,
)

case class Province(no: Int, name: String)

case class City(no: Int, name: String)

object CitiesData:
  lazy val data = Map(
    Province(1, "P1") -> List(
      City(11, "C11"),
      City(12, "C12"),
      City(13, "C13"),
      City(14, "C14"),
      City(15, "C15"),
      City(16, "C16"),
    ),
    Province(2, "P2") -> List(
      City(21, "C21"),
      City(22, "C22"),
      City(23, "C23"),
      City(24, "C24"),
      City(25, "C25"),
      City(26, "C26"),
      City(27, "C27"),
    ),
    Province(3, "P3") -> List(
      City(31, "C31"),
      City(32, "C32"),
      City(33, "C33"),
      City(34, "C34"),
      City(35, "C35"),
      City(36, "C36"),
      City(37, "C37"),
    ),
    Province(4, "P4") -> List(
      City(41, "C41"),
      City(42, "C42"),
      City(43, "C43"),
      City(34, "C44"),
      City(35, "C45"),
    ),
  )

class BasicFormExamplePage extends MultipleCodeExamples2Page:
  def pageTitle: String = "Simple Form Example"

  def renderContentsWithSnippets()(implicit fsc: FSContext): Unit =
    renderSnippet("Source"):

      import DefaultFSDemoBSForm7Renderers.given
      import com.fastscala.templates.bootstrap5.helpers.BSHelpers.{ given, * }
      var editing = new User1(
        firstName = "",
        lastName = "",
        email = "",
        phoneNumber = "",
        securityLevel = 0,
        countryOfResidence = CountriesData.data(0),
        birthDay = Some("2022-08-04"),
        province = CitiesData.data.head._1,
        city = CitiesData.data.head._2.head,
      )

      div.border.p_2.rounded.apply:
        new DefaultForm7:
          override def postSubmitForm()(using FSContext): Js =
            BSModal5.verySimple("Created User", "Done"): modal =>
              _ =>
                <span><b>First Name:</b> {editing.firstName}</span><br/> ++
                  <span><b>Last Name:</b> {editing.lastName}</span><br/> ++
                  <span><b>Email:</b> {editing.email}</span><br/> ++
                  <span><b>Phone:</b> {editing.phoneNumber}</span><br/> ++
                  <span><b>Security Level:</b> {editing.securityLevel}</span><br/> ++
                  <span><b>Country of Residence:</b> {
                    editing.countryOfResidence.name.common
                  }</span><br/> ++
                  <span><b>Date of Birth:</b> {editing.birthDay.getOrElse("N/A")}</span><br/> ++
                  <span><b>Province:</b> {
                    s"${editing.province.name}(${editing.province.no})"
                  }</span><br/> ++
                  <span><b>City:</b> {s"${editing.city.name}(${editing.city.no})"}</span>

          lazy val _provField = F7SelectField[Province](CitiesData.data.keys.toList.sortBy(_.no))
            .label("Province")
            .rw(editing.province, editing.province = _)
            .option2String(_.name)

          lazy val rootField: F7Field = F7VerticalField(
            F7ContainerField("row")(
              "col" -> F7StringField().label("First Name").rw(editing.firstName, editing.firstName = _),
              "col" -> F7StringField().label("Last Name").rw(editing.lastName, editing.lastName = _),
            ),
            F7StringField().label("Email").rw(editing.email, editing.email = _).inputType("email"),
            F7StringField()
              .label("Phone Number")
              .rw(editing.phoneNumber, editing.phoneNumber = _)
              .inputType("tel"),
            F7SelectField[Country](CountriesData.data.toList)
              .label("Country of Residence")
              .rw(editing.countryOfResidence, editing.countryOfResidence = _)
              .option2String(_.name.common),
            F7IntOptField()
              .label("Security Level")
              .rw(Some(editing.securityLevel), oi => editing.securityLevel = oi.getOrElse(0)),
            F7LocalDateOptField(editing.birthDay, editing.birthDay = _).label("BirthDay"),
            _provField,
            F7SelectField[City](() => CitiesData.data(_provField.currentValue))
              .label("City")
              .rw(editing.city, editing.city = _)
              .option2String(_.name)
              .deps(Set(_provField)),
            F7SubmitButtonField(_ => BSBtn().BtnPrimary.lbl("Create User").btn.d_block),
          )
        .render()

    closeSnippet()
