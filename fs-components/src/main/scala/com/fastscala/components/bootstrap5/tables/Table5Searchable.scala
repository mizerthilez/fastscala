package com.fastscala.components.bootstrap5.tables

import com.fastscala.utils.toOption

/** Search syntax:
  * abc    ----- any column's value contains "abc"
  * c:abc  ----- column name equal to 'c' and its value contains "abc"
  * :abc   ----- same as abc
  * c:     ----- empty content which is ignored
  * (a,b,c)     ----- any column's value equals to (not contains) "a" or "b" or "c".
  * c:(a,b,c)   ----- column name equal to 'c' and its value equals to (not contains) "a" or "b" or "c"
  * [10,100]    ----- any column has numeric value, and its value between 10 and 100
  * c:[10,100]  ----- column name equal to 'c' and it has numeric value, and its value between 10 and 100
  * [10,]  ----- any column has numeric value, and its value >= 10
  * [,100] ----- any column has numeric value, and its value <= 100
  * A B C  ----- condition A AND condition B AND condition C
  *
  * all textual comparions are case sentitive.
  */
trait Table5Searchable extends Table5Base with Table5SeqDataSource with Table5StandardColumns:
  def colSearchers: Seq[String => R => Boolean] = columns().collect:
    case ColStr(title, render) =>
      input =>
        row =>
          input match
            case s"$t:$c" =>
              if t.nonEmpty && t != title then false else contains(render(row), c)
            case _ =>
              contains(render(row), input)

  def contains(content: String, c: String): Boolean =
    if c.size < 2 || c(0) != '(' && c(0) != '[' then content.contains(c)
    else if c(0) == '(' then
      if c.last == ')' then c.tail.init.split(',').filter(_.nonEmpty).exists(_ == content) else false
    else
      content.toDoubleOption
        .flatMap: num =>
          c match
            case s"[,$c2]" =>
              c2.toDoubleOption.map(n2 => num <= n2)
            case s"[$c1,]" =>
              c1.toDoubleOption.map(n1 => n1 <= num)
            case s"[$c1,$c2]" =>
              c1.toDoubleOption.flatMap(n1 => c2.toDoubleOption.map(n2 => n1 <= num && num <= n2))
            case _ => None
        .getOrElse(false)

  extension (seq: Seq[R]) def searchFor(value: String): Seq[R] = search(seq, value)

  def search(seq: Seq[R], value: String): Seq[R] =
    value
      .split(' ')
      .filter:
        case "" => false
        case s"$t:$c" if c.isEmpty => false
        case _ => true
      .toOption
      .map: tokens =>
        val searchs = colSearchers
        seq.filter: row =>
          tokens.forall: token =>
            searchs.exists(_(token)(row))
      .getOrElse(seq)
