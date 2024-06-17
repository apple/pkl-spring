import org.gradle.plugins.ide.idea.model.IdeaLanguageLevel

plugins {
  idea
}

idea {
  project {
    languageLevel = IdeaLanguageLevel("17")
    jdkName = "17"
  }
}