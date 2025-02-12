package com.fastscala.components.form7

import java.net.URLEncoder

import scala.jdk.CollectionConverters.MapHasAsScala

import org.eclipse.jetty.server.Request

import com.fastscala.core.FSContext
import com.fastscala.js.Js
import com.fastscala.components.form7.mixins.QuerySerializableStringF7Field

trait QueryStringSavedForm extends Form7:
  override def initForm()(using fsc: FSContext): Unit =
    super.initForm()
    rootField
      .fieldAndChildrenMatchingPredicate(_ => true)
      .foreach:
        case f: QuerySerializableStringF7Field =>
          Option(Request.getParameters(fsc.page.req).getValue(f.queryStringParamName)).foreach: str =>
            f.loadFromString(str)
        case _ =>

  override def postSubmitForm()(using fsc: FSContext): Js =
    super.postSubmitForm() `&`:
      val newParams = rootField
        .fieldAndChildrenMatchingPredicate(_ => true)
        .collect:
          case f: QuerySerializableStringF7Field => f.queryStringParamName -> f.saveToString().toArray
        .toMap
      val existingParams =
        (Request.getParameters(fsc.page.req).toStringArrayMap.asScala -- newParams.keys).toMap
      Js.redirectTo(
        fsc.page.req.getHttpURI.getPath + "?" +
          (existingParams ++ newParams)
            .flatMap:
              case (key, values) =>
                values
                  .map(v => URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(v, "UTF-8"))
                  .toList
            .mkString("&")
      )
