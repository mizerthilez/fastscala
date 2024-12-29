package com.fastscala.utils

given ContextFunc_2_NormalFunc_Conversion[A, B]: Conversion[A ?=> B, A => B] =
  contextFunc => contextFunc(using _)

extension (s: String) def toOption: Option[String] = Some(s).filter(_.nonEmpty)

extension [A](s: Seq[A]) def toOption: Option[Seq[A]] = Some(s).filter(_.nonEmpty)
