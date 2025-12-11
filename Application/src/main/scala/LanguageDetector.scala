import org.apache.spark.sql.SparkSession
import scala.collection.Map

object LanguageDetector {
  
  // Rudimentary lists of common "stop words"
  val englishWords = Set("the", "a", "an", "is", "of", "and", "to")
  val frenchWords  = Set("le", "la", "les", "un", "une", "de", "et")
  val germanWords  = Set("der", "die", "das", "ein", "eine", "und", "zu")

  def main(args: Array[String]): Unit = {
    if (args.length < 1) {
      System.err.println("Usage: LanguageDetector <HDFS_input_path>")
      System.exit(1)
    }

    val spark = SparkSession.builder.appName("LanguageDetector").getOrCreate()
    
    val inputPath = args(0)   

    // 1. Load the text files from HDFS into an RDD
    val textRDD = spark.sparkContext.textFile(inputPath)

    // 2. Process the text: flatMap to words, filter out non-alphabetic, lowercase
    val wordCounts: Map[String, Long] = textRDD
      .flatMap(_.split("\\s+"))
      .filter(_.matches("^[a-zA-Z]+$")) // Simple filter for words
      .map(_.toLowerCase)
      .countByValue() // Count the occurrences of each unique word

    // 3. Score the content against the language lists
    var englishScore: Long = 0
    var frenchScore: Long = 0
    var germanScore: Long = 0

    // Check words against our simple language lists
    wordCounts.foreach { case (word, count) =>
      if (englishWords.contains(word)) englishScore += count
      if (frenchWords.contains(word)) frenchScore += count
      if (germanWords.contains(word)) germanScore += count
    }

    // 4. Determine the most common language
    val scores = Map("English" -> englishScore, "French" -> frenchScore, "German" -> germanScore)
    val (mostLikelyLanguage, score) = scores.maxBy(_._2)

    println("--- Language Detection Results ---")
    println(s"English Score: $englishScore")
    println(s"French Score: $frenchScore")
    println(s"German Score: $germanScore")
    println(s"The most common language in $inputPath is: **$mostLikelyLanguage** (Score: $score)")
    println("------------------------------------")

    spark.stop()
  }
}