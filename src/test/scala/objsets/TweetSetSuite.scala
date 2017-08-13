package objsets

import objsets.GoogleVsApple.{appleTweets, googleTweets}
import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class TweetSetSuite extends FunSuite {
  trait TestSets {
    val set1 = new Empty
    val set2 = set1.incl(new Tweet("a", "a body", 20))
    val set3 = set2.incl(new Tweet("b", "b body", 20))
    val c = new Tweet("c", "c body", 7)
    val d = new Tweet("d", "d body", 9)
    val set4c = set3.incl(c)
    val set4d = set3.incl(d)
    val set5 = set4c.incl(d)
  }

  def asSet(tweets: TweetSet): Set[Tweet] = {
    var res = Set[Tweet]()
    tweets.foreach(res += _)
    res
  }

  def size(set: TweetSet): Int = asSet(set).size

  test("filter: on empty set") {
    new TestSets {
      assert(size(set1.filter(tw => tw.user == "a")) === 0)
    }
  }

  test("filter: one elem set") {
    new TestSets {
      assert(size(set2.filter(tw => tw.user == "a")) === 1)
    }
  }

  test("filter: two elem set") {
    new TestSets {
      assert(size(set3.filter(tw => tw.user == "b")) === 1)
      assert(size(set3
                    .remove(new Tweet("a", "a body", 20))
                    .incl(new Tweet("a", "a body", 20))
                    .filter(tw => tw.user == "a")) === 1)
    }
  }

  test("filter: a on set5") {
    new TestSets {
      assert(size(set5.filter(tw => tw.user == "a")) === 1)
    }
  }

  test("filter: 20 on set5") {
    new TestSets {
      assert(size(set5.filter(tw => tw.retweets == 20)) === 2)
    }
  }

  test("union: set4c and set4d") {
    new TestSets {
      assert(size(set4c.union(set4d)) === 4)
    }
  }

  test("union: with empty set (1)") {
    new TestSets {
      assert(size(set5.union(set1)) === 4)
    }
  }

  test("union: with empty set (2)") {
    new TestSets {
      assert(size(set1.union(set5)) === 4)
    }
  }

  test("mostRetweeted: Empty") {
    new TestSets {
      intercept[NoSuchElementException] {
        set1.mostRetweeted
      }
    }
  }

  test("mostRetweeted: NonEmpty") {
    new TestSets {
      assert(set5.mostRetweeted.retweets === 20)
    }
  }

  test("descending: set5") {
    new TestSets {
      val trends = set5.descendingByRetweet
      assert(!trends.isEmpty)
      assert(trends.head.user == "a" || trends.head.user == "b")
    }
  }

  test("googleTweets") {
    assert(googleTweets
      .filter(tw => tw.text.equals("iPhone 5 vs Galaxy S III: Who's screen is prettier? http://t.co/n6CbaspY"))
      .contains(new Tweet("gizmodo", "iPhone 5 vs Galaxy S III: Who's screen is prettier? http://t.co/n6CbaspY", 108))
    )
  }

  test("appleTweets") {
    assert(appleTweets
      .filter(tw => tw.text.equals("Switched On: iOS 6 gets back from the app -  http://t.co/8j4YL4Yn"))
      .contains(new Tweet("engadget", "Switched On: iOS 6 gets back from the app -  http://t.co/8j4YL4Yn", 16))
    )
  }

  test("trending: allTweets") {
    assert(GoogleVsApple.trending.head.retweets == 345)
  }

}
