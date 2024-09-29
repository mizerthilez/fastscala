package dev.ironduck.working.db

object FakeDB:
  val users = collection.mutable.ListBuffer[User](
    new User("John", "Doe", "admin", loginToken = "f5938e4f-caf7-4b6a-a0d2-46377f22e055")
      .setPassword("admin")
  )
