package scalikejdbc.interpolation

import org.scalatest._
import org.joda.time.DateTime

class SQLSyntaxSpec extends FlatSpec with Matchers {

  import Implicits._

  behavior of "SQLSyntax"

  it should "be available" in {
    SQLSyntax("where") should not be (null)
  }

  it should "have #append" in {
    val s = SQLSyntax.eq(sqls"id", 123).append(sqls"and name is not null")
    s.value should equal(" id = ? and name is not null")
    s.parameters should equal(Seq(123))
  }

  it should "have #join" in {
    val s = SQLSyntax.join(Seq(sqls"id", sqls"name"), sqls"and")
    s.value should equal("id and name")
    s.parameters should equal(Nil)
  }

  it should "have #csv" in {
    val (id, name) = (123, "Alice")
    val s = SQLSyntax.csv(sqls"id = ${id}", sqls"name = ${name}")
    s.value should equal("id = ?, name = ?")
    s.parameters should equal(Seq(123, "Alice"))
  }

  it should "have #eq" in {
    val s = SQLSyntax.eq(sqls"id", 123)
    s.value should equal(" id = ?")
    s.parameters should equal(Seq(123))
  }

  it should "have #eq and #ne" in {
    val s = SQLSyntax.eq(sqls"id", 123).and.ne(sqls"name", "Alice")
    s.value should equal(" id = ? and name <> ?")
    s.parameters should equal(Seq(123, "Alice"))
  }

  it should "have #gt" in {
    val s = SQLSyntax.gt(sqls"amount", 200)
    s.value should equal(" amount > ?")
    s.parameters should equal(Seq(200))
  }

  it should "have #ge" in {
    val s = SQLSyntax.ge(sqls"amount", 200)
    s.value should equal(" amount >= ?")
    s.parameters should equal(Seq(200))
  }

  it should "have #lt" in {
    val s = SQLSyntax.lt(sqls"amount", 200)
    s.value should equal(" amount < ?")
    s.parameters should equal(Seq(200))
  }

  it should "have #le" in {
    val s = SQLSyntax.le(sqls"amount", 200)
    s.value should equal(" amount <= ?")
    s.parameters should equal(Seq(200))
  }

  it should "have #isNull" in {
    val s = SQLSyntax.isNull(sqls"amount")
    s.value should equal(" amount is null")
    s.parameters should equal(Nil)
  }

  it should "have #isNotNull" in {
    val s = SQLSyntax.isNotNull(sqls"amount")
    s.value should equal(" amount is not null")
    s.parameters should equal(Nil)
  }

  it should "have #in" in {
    val s = SQLSyntax.in(sqls"id", Seq(1, 2, 3))
    s.value should equal(" id in (?, ?, ?)")
    s.parameters should equal(Seq(1, 2, 3))
  }

  it should "have #in with empty" in {
    val s = SQLSyntax.in(sqls"id", Seq())
    s.value should equal(" FALSE")
    s.parameters should equal(Seq())
  }

  it should "have #in for 2 columns" in {
    val s = SQLSyntax.in((sqls"id", sqls"name"), Seq((1, "Alice"), (2, "Bob")))
    s.value should equal(" (id, name) in ((?, ?), (?, ?))")
    s.parameters should equal(Seq(1, "Alice", 2, "Bob"))
  }

  it should "have #in for 2 columns with empty" in {
    val s = SQLSyntax.in((sqls"id", sqls"name"), Seq())
    s.value should equal(" FALSE")
    s.parameters should equal(Seq())
  }

  it should "have #in for 3 columns" in {
    val s = SQLSyntax.in((sqls"id", sqls"name", sqls"age"), Seq((1, "Alice", 20), (2, "Bob", 23)))
    s.value should equal(" (id, name, age) in ((?, ?, ?), (?, ?, ?))")
    s.parameters should equal(Seq(1, "Alice", 20, 2, "Bob", 23))
  }
  it should "have #in for 3 columns with empty" in {
    val s = SQLSyntax.in((sqls"id", sqls"name", sqls"age"), Seq())
    s.value should equal(" FALSE")
    s.parameters should equal(Seq())
  }
  it should "have #in for 4 columns" in {
    val s = SQLSyntax.in((sqls"id", sqls"name", sqls"age", sqls"foo"), Seq((1, "Alice", 20, "bar"), (2, "Bob", 23, "baz")))
    s.value should equal(" (id, name, age, foo) in ((?, ?, ?, ?), (?, ?, ?, ?))")
    s.parameters should equal(Seq(1, "Alice", 20, "bar", 2, "Bob", 23, "baz"))
  }
  it should "have #in for 4 columns with empty" in {
    val s = SQLSyntax.in((sqls"id", sqls"name", sqls"age", sqls"foo"), Seq())
    s.value should equal(" FALSE")
    s.parameters should equal(Seq())
  }
  it should "have #in for 5 columns" in {
    val time = DateTime.now
    val s = SQLSyntax.in((sqls"id", sqls"name", sqls"age", sqls"foo", sqls"created_at"), Seq((1, "Alice", 20, "bar", null), (2, "Bob", 23, "baz", time)))
    s.value should equal(" (id, name, age, foo, created_at) in ((?, ?, ?, ?, ?), (?, ?, ?, ?, ?))")
    s.parameters should equal(Seq(1, "Alice", 20, "bar", null, 2, "Bob", 23, "baz", time))
  }
  it should "have #in for 5 columns with empty" in {
    val time = DateTime.now
    val s = SQLSyntax.in((sqls"id", sqls"name", sqls"age", sqls"foo", sqls"created_at"), Seq())
    s.value should equal(" FALSE")
    s.parameters should equal(Seq())
  }

  it should "have #notIn" in {
    val s = SQLSyntax.notIn(sqls"id", Seq(1, 2, 3))
    s.value should equal(" id not in (?, ?, ?)")
    s.parameters should equal(Seq(1, 2, 3))
  }
  it should "have #notIn woth empty" in {
    val s = SQLSyntax.notIn(sqls"id", Seq())
    s.value should equal(" TRUE")
    s.parameters should equal(Seq())
  }

  it should "have #notIn for 2 columns" in {
    val s = SQLSyntax.notIn((sqls"id", sqls"name"), Seq((1, "Alice"), (2, "Bob")))
    s.value should equal(" (id, name) not in ((?, ?), (?, ?))")
    s.parameters should equal(Seq(1, "Alice", 2, "Bob"))
  }
  it should "have #notIn for 2 columns with empty" in {
    val s = SQLSyntax.notIn((sqls"id", sqls"name"), Seq())
    s.value should equal(" TRUE")
    s.parameters should equal(Seq())
  }
  it should "have #notIn for 3 columns" in {
    val s = SQLSyntax.notIn((sqls"id", sqls"name", sqls"age"), Seq((1, "Alice", 20), (2, "Bob", 23)))
    s.value should equal(" (id, name, age) not in ((?, ?, ?), (?, ?, ?))")
    s.parameters should equal(Seq(1, "Alice", 20, 2, "Bob", 23))
  }
  it should "have #notIn for 3 columns with empty" in {
    val s = SQLSyntax.notIn((sqls"id", sqls"name", sqls"age"), Seq())
    s.value should equal(" TRUE")
    s.parameters should equal(Seq())
  }
  it should "have #notIn for 4 columns" in {
    val s = SQLSyntax.notIn((sqls"id", sqls"name", sqls"age", sqls"foo"), Seq((1, "Alice", 20, "bar"), (2, "Bob", 23, "baz")))
    s.value should equal(" (id, name, age, foo) not in ((?, ?, ?, ?), (?, ?, ?, ?))")
    s.parameters should equal(Seq(1, "Alice", 20, "bar", 2, "Bob", 23, "baz"))
  }
  it should "have #notIn for 4 columns with empty" in {
    val s = SQLSyntax.notIn((sqls"id", sqls"name", sqls"age", sqls"foo"), Seq())
    s.value should equal(" TRUE")
    s.parameters should equal(Seq())
  }
  it should "have #notIn for 5 columns" in {
    val time = DateTime.now
    val s = SQLSyntax.notIn((sqls"id", sqls"name", sqls"age", sqls"foo", sqls"created_at"), Seq((1, "Alice", 20, "bar", null), (2, "Bob", 23, "baz", time)))
    s.value should equal(" (id, name, age, foo, created_at) not in ((?, ?, ?, ?, ?), (?, ?, ?, ?, ?))")
    s.parameters should equal(Seq(1, "Alice", 20, "bar", null, 2, "Bob", 23, "baz", time))
  }
  it should "have #notIn for 5 columns with empty" in {
    val time = DateTime.now
    val s = SQLSyntax.notIn((sqls"id", sqls"name", sqls"age", sqls"foo", sqls"created_at"), Seq())
    s.value should equal(" TRUE")
    s.parameters should equal(Seq())
  }

  it should "have #in with subQuery" in {
    val s = SQLSyntax.in(sqls"id", sqls"select id from users where deleted = ${false}")
    s.value should equal(" id in (select id from users where deleted = ?)")
    s.parameters should equal(Seq(false))
  }

  it should "have #notIn with subQuery" in {
    val s = SQLSyntax.notIn(sqls"id", sqls"select id from users where deleted = ${false}")
    s.value should equal(" id not in (select id from users where deleted = ?)")
    s.parameters should equal(Seq(false))
  }

  it should "have #like" in {
    val s = SQLSyntax.like(sqls"name", "%abc%")
    s.value should equal(" name like ?")
    s.parameters should equal(Seq("%abc%"))
  }

  it should "have #notLike" in {
    val s = SQLSyntax.notLike(sqls"name", "%abc%")
    s.value should equal(" name not like ?")
    s.parameters should equal(Seq("%abc%"))
  }

  it should "have #exists with subQuery" in {
    val s = SQLSyntax.exists(sqls"select id from users where deleted = ${false}")
    s.value should equal(" exists (select id from users where deleted = ?)")
    s.parameters should equal(Seq(false))
  }

  it should "have #notExists with subQuery" in {
    val s = SQLSyntax.notExists(sqls"select id from users where deleted = ${false}")
    s.value should equal(" not exists (select id from users where deleted = ?)")
    s.parameters should equal(Seq(false))
  }

  it should "have #groupBy and #having" in {
    val groupId = 123
    val s = SQLSyntax.groupBy(sqls"name").having(sqls"group_id = ${groupId}")
    s.value should equal(" group by name having group_id = ?")
    s.parameters should equal(Seq(123))
  }

  it should "have #orderBy" in {
    val s = SQLSyntax.orderBy(sqls"id")
    s.value should equal(" order by id")
    s.parameters should equal(Nil)
  }

  it should "have #orderBy for empty values" in {
    {
      val s = SQLSyntax.orderBy(sqls"")
      s.value should equal("")
      s.parameters should equal(Nil)
    }
    {
      val s = SQLSyntax.orderBy()
      s.value should equal("")
      s.parameters should equal(Nil)
    }
    {
      val s = SQLSyntax.orderBy(Nil: _*)
      s.value should equal("")
      s.parameters should equal(Nil)
    }
  }

  it should "have #orderBy and #asc" in {
    val s = SQLSyntax.orderBy(sqls"id").asc
    s.value should equal(" order by id asc")
    s.parameters should equal(Nil)
  }

  it should "have #orderBy and #desc" in {
    val s = SQLSyntax.orderBy(sqls"id").desc
    s.value should equal(" order by id desc")
    s.parameters should equal(Nil)
  }

  it should "have #limit and #offset" in {
    val s = SQLSyntax.limit(10).offset(20)
    s.value should equal(" limit 10 offset 20")
    s.parameters should equal(Nil)
  }

  it should "have #where" in {
    val s = SQLSyntax.where
    s.value should equal(" where")
    s.parameters should equal(Nil)
  }

  it should "have #where(SQLSyntax)" in {
    val id = 123
    val s = SQLSyntax.where(sqls"id = ${id}")
    s.value should equal(" where id = ?")
    s.parameters should equal(Seq(123))
  }

  it should "have #and" in {
    val (id, name) = (123, "Alice")
    val s = SQLSyntax.eq(sqls"id", id).and.eq(sqls"name", name)
    s.value should equal(" id = ? and name = ?")
    s.parameters should equal(Seq(123, "Alice"))
  }

  it should "have #or" in {
    val (id, name) = (123, "Alice")
    val s = SQLSyntax.eq(sqls"id", id).or.eq(sqls"name", name)
    s.value should equal(" id = ? or name = ?")
    s.parameters should equal(Seq(123, "Alice"))
  }

  it should "have #toAndConditionOpt (Some)" in {
    val (id, name) = (123, "Alice")
    val s = SQLSyntax.toAndConditionOpt(Some(sqls"id = ${id}"), Some(sqls"name = ${name} or name is null")).get
    s.value should equal("id = ? and (name = ? or name is null)")
    s.parameters should equal(Seq(123, "Alice"))
  }

  it should "have #toAndConditionOpt (None)" in {
    SQLSyntax.toAndConditionOpt(None).isDefined should equal(false)
    SQLSyntax.toAndConditionOpt(None, None).isDefined should equal(false)
    SQLSyntax.toAndConditionOpt(None, None, None).isDefined should equal(false)
  }

  it should "have #toOrConditionOpt (Some)" in {
    val (id, name) = (123, "Alice")
    val s = SQLSyntax.toOrConditionOpt(Some(sqls"id = ${id}"), Some(sqls"name = ${name} or name is null")).get
    s.value should equal("id = ? or (name = ? or name is null)")
    s.parameters should equal(Seq(123, "Alice"))
  }

  it should "have #toOrConditionOpt (None)" in {
    SQLSyntax.toOrConditionOpt(None).isDefined should equal(false)
    SQLSyntax.toOrConditionOpt(None, None).isDefined should equal(false)
    SQLSyntax.toOrConditionOpt(None, None, None).isDefined should equal(false)
  }

  it should "have #equals as expected" in {
    val (id, id2, name) = (123, 234, "Alice")
    val sqls1 = sqls"id = ${id} and name = ${name}"
    val sqls2 = sqls"id = ${id} and name = ${name}"
    val sqls3 = sqls"id = ${id2} and name = ${name}"
    val sqls4 = sqls"id = ${id} and name = ${name} and deleted_at is null"
    sqls1 == sqls2 should be(true)
    sqls2 == sqls3 should be(false)
    sqls2 == sqls4 should be(false)
  }

  it should "have joinWithAnd" in {
    val s1 = SQLSyntax.joinWithAnd(sqls"a = ${123}", sqls"b is not null")
    s1.value should equal("a = ? and b is not null")
    s1.parameters should equal(Seq(123))

    val s2 = SQLSyntax.joinWithAnd(sqls"a = ${123}", sqls"b = ${234} or c = ${345}", sqls"d is not null", sqls"E IS NULL OR F IS NOT NULL")
    s2.value should equal("a = ? and (b = ? or c = ?) and d is not null and (E IS NULL OR F IS NOT NULL)")
    s2.parameters should equal(Seq(123, 234, 345))
  }

  it should "have joinWithOr" in {
    val s1 = SQLSyntax.joinWithOr(sqls"a = ${123}", sqls"b is not null")
    s1.value should equal("a = ? or b is not null")
    s1.parameters should equal(Seq(123))

    val s2 = SQLSyntax.joinWithOr(sqls"a = ${123}", sqls"b = ${234} or c = ${345}", sqls"d is not null", sqls"E IS NULL OR F IS NOT NULL")
    s2.value should equal("a = ? or (b = ? or c = ?) or d is not null or (E IS NULL OR F IS NOT NULL)")
    s2.parameters should equal(Seq(123, 234, 345))
  }

  it should "have createUnsafe" in {
    val columnName = "foo"
    val v = SQLSyntax.createUnsafely(s"${columnName} = ?")
    v should equal(SQLSyntax("foo = ?"))
  }

}
