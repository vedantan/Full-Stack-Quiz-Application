import i18n from "i18next";
import { initReactI18next } from "react-i18next";
import LanguageDetector from "i18next-browser-languagedetector";

const resources = {
  en: {
    translation: {
      navbar: {
        home: "Home",
        questions: "Questions",
        addQuestion: "Add Question",
        quiz: "Quiz",
        leaderboard: "Leaderboard",
        profile: "Profile"
      },
      questionBank: "Question Bank",
      selectCategory: "Select Category",
      generateQuiz: "Generate Quiz"
    }
  },

  hi: {
    translation: {
      navbar: {
        home: "होम",
        questions: "प्रश्न",
        addQuestion: "प्रश्न जोड़ें",
        quiz: "क्विज़",
        leaderboard: "लीडरबोर्ड",
        profile: "प्रोफाइल"
      },
      questionBank: "प्रश्न बैंक",
      selectCategory: "श्रेणी चुनें",
      generateQuiz: "क्विज़ बनाएं"
    }
  },

  mr: {
    translation: {
      navbar: {
        home: "मुख्यपृष्ठ",
        questions: "प्रश्न",
        addQuestion: "प्रश्न जोडा",
        quiz: "क्विझ",
        leaderboard: "लीडरबोर्ड",
        profile: "प्रोफाइल"
      },
      questionBank: "प्रश्न बँक",
      selectCategory: "श्रेणी निवडा",
      generateQuiz: "क्विझ तयार करा"
    }
  }
};

i18n
  .use(LanguageDetector)
  .use(initReactI18next)
  .init({
    resources,
    fallbackLng: "en",

    detection: {
      order: ["localStorage", "navigator"],
      caches: ["localStorage"]
    },

    interpolation: {
      escapeValue: false
    }
  });

export default i18n;