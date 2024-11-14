package com.fastscala.utils

given ContextFunc_2_NormalFunc_Conversion[A, B]: Conversion[A ?=> B, A => B] =
  contextFunc => contextFunc(using _)
