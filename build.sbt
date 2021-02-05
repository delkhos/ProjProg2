libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "3.0.0"

fork := true

javaOptions ++= Seq(
    "-Dsun.java2d.opengl=true"
)
