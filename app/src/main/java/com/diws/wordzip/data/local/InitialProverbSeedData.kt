package com.diws.wordzip.data.local

object InitialProverbSeedData {
    fun getInitialProverbs(): List<ProverbEntity> {
        return listOf(
            ProverbEntity(
                id = "proverb_1",
                englishText = "Actions speak louder than words.",
                hindiText = "बातों से ज्यादा काम बोलता है।",
                hindiEquivalent = "कथनी से करनी भली।",
                meaningEnglish = "What you do is more important than what you say.",
                meaningHindi = "सिर्फ बातें करने से कुछ नहीं होता, आपका काम असलियत दिखाता है।",
                example = "Don't just promise you'll study harder; actions speak louder than words.",
                category = "Action"
            ),
            ProverbEntity(
                id = "proverb_2",
                englishText = "A stitch in time saves nine.",
                hindiText = "समय पर किया गया छोटा सुधार भविष्य की बड़ी परेशानी से बचाता है।",
                hindiEquivalent = "समय चूकि पुनि का पछिताने।",
                meaningEnglish = "Fixing a small problem immediately prevents it from becoming a huge disaster later.",
                meaningHindi = "शुरुआत में ही समस्या हल कर लेने से बाद में भारी नुकसान नहीं होता।",
                example = "Fix that small leak in the roof today; remember, a stitch in time saves nine.",
                category = "Wisdom"
            ),
            ProverbEntity(
                id = "proverb_3",
                englishText = "Where there is a will, there is a way.",
                hindiText = "जहाँ चाह, वहाँ राह।",
                hindiEquivalent = "मन के हारे हार है, मन के जीते जीत।",
                meaningEnglish = "If you are determined enough to do something, you will find a way to achieve it.",
                meaningHindi = "यदि किसी काम को करने का दृढ़ संकल्प हो, तो कोई न कोई रास्ता निकल ही आता है।",
                example = "Even though he had no money for college, where there is a will, there is a way—he won a scholarship.",
                category = "Success"
            ),
            ProverbEntity(
                id = "proverb_4",
                englishText = "All that glitters is not gold.",
                hindiText = "हर चमकती चीज सोना नहीं होती।",
                hindiEquivalent = "हाथी के दांत खाने के और दिखाने के और।",
                meaningEnglish = "Things that look attractive or valuable on the surface may not be good in reality.",
                meaningHindi = "दिखावे पर विश्वास नहीं करना चाहिए, आंतरिक गुण ही असली होते हैं।",
                example = "The cheap phone looked premium, but it stopped working in a week. All that glitters is not gold.",
                category = "Wisdom"
            ),
            ProverbEntity(
                id = "proverb_5",
                englishText = "Better late than never.",
                hindiText = "देर आए दुरुस्त आए।",
                hindiEquivalent = "देर भली पर अंधेर नहीं।",
                meaningEnglish = "It is better to do something late than to never do it at all.",
                meaningHindi = "किसी काम को बिल्कुल न करने से अच्छा है कि उसे देर से ही सही, पूरा कर लिया जाए।",
                example = "He started learning to swim at age 40, but better late than never.",
                category = "Life"
            ),
            ProverbEntity(
                id = "proverb_6",
                englishText = "Every cloud has a silver lining.",
                hindiText = "हर मुश्किल के पीछे एक उम्मीद की किरण होती है।",
                hindiEquivalent = "दुख के बाद सुख आता ही है।",
                meaningEnglish = "Every difficult or unpleasant situation has some positive or hopeful aspect.",
                meaningHindi = "बुरी से बुरी परिस्थिति में भी कोई न कोई भलाई छिपी होती है।",
                example = "Losing that job led him to start his own dream company—every cloud has a silver lining.",
                category = "Mindset"
            ),
            ProverbEntity(
                id = "proverb_7",
                englishText = "Honesty is the best policy.",
                hindiText = "ईमानदारी सबसे अच्छी नीति है।",
                hindiEquivalent = "सांच को आंच नहीं।",
                meaningEnglish = "Telling the truth and being authentic is always the most honorable course of action.",
                meaningHindi = "सच्चाई और ईमानदारी से काम करने पर कभी शर्मिंदा नहीं होना पड़ता।",
                example = "He admitted his mistake right away because honesty is the best policy.",
                category = "Wisdom"
            ),
            ProverbEntity(
                id = "proverb_8",
                englishText = "Practice makes a man perfect.",
                hindiText = "अभ्यास से ही इंसान निपुण बनता है।",
                hindiEquivalent = "करत-करत अभ्यास के, जड़मति होत सुजान।",
                meaningEnglish = "Repeated practice and effort will lead to mastery in any skill.",
                meaningHindi = "लगातार मेहनत और अभ्यास करने से किसी भी कठिन कार्य में महारत हासिल की जा सकती है।",
                example = "Keep practicing your public speaking; remember, practice makes perfect.",
                category = "Success"
            ),
            ProverbEntity(
                id = "proverb_9",
                englishText = "Rome was not built in a day.",
                hindiText = "महान कार्य पूरे होने में समय लगता है।",
                hindiEquivalent = "हथेली पर सरसों नहीं जमती।",
                meaningEnglish = "Important achievements take continuous time, patience, and persistent effort.",
                meaningHindi = "बड़ी सफलता एक दिन में नहीं मिलती, उसके लिए धैर्य और निरंतर प्रयास चाहिए।",
                example = "Don't be discouraged if your business takes time to grow; Rome was not built in a day.",
                category = "Patience"
            ),
            ProverbEntity(
                id = "proverb_10",
                englishText = "Barking dogs seldom bite.",
                hindiText = "जो गरजते हैं, वो बरसते नहीं।",
                hindiEquivalent = "भौंकने वाले कुत्ते काटते नहीं।",
                meaningEnglish = "People who make excessive threats rarely take any actual harmful action.",
                meaningHindi = "जो लोग केवल डींगें हांकते हैं या डराते हैं, वे वास्तव में कुछ नहीं कर पाते।",
                example = "Don't worry about his angry threats; barking dogs seldom bite.",
                category = "Life"
            ),
            ProverbEntity(
                id = "proverb_11",
                englishText = "Birds of a feather flock together.",
                hindiText = "एक जैसे स्वभाव वाले लोग साथ रहते हैं।",
                hindiEquivalent = "चोर-चोर मौसेरे भाई।",
                meaningEnglish = "People with similar interests, habits, or character tend to spend time together.",
                meaningHindi = "समान विचार और आदत वाले व्यक्ति एक-दूसरे की संगति पसंद करते हैं।",
                example = "All the coders gathered at the same table—birds of a feather flock together.",
                category = "Friendship"
            ),
            ProverbEntity(
                id = "proverb_12",
                englishText = "Look before you leap.",
                hindiText = "बिना सोचे-समझे कोई कदम मत उठाओ।",
                hindiEquivalent = "बिना विचारे जो करे, सो पाछे पछताय।",
                meaningEnglish = "Carefully consider the consequences and risks before making an important decision.",
                meaningHindi = "कोई भी बड़ा निर्णय लेने से पहले उसके अच्छे और बुरे परिणामों पर विचार कर लें।",
                example = "Read the investment contract thoroughly; always look before you leap.",
                category = "Wisdom"
            ),
            ProverbEntity(
                id = "proverb_13",
                englishText = "A friend in need is a friend indeed.",
                hindiText = "सच्चा मित्र वही है जो मुसीबत में काम आए।",
                hindiEquivalent = "धीरज, धर्म, मित्र अरु नारी, आपद काल परखिए चारी।",
                meaningEnglish = "Someone who helps you when you are in trouble is a true and genuine friend.",
                meaningHindi = "कठिन समय में साथ निभाने वाला ही सच्चा मित्र होता है।",
                example = "When I was ill, Rahul took care of all my errands. Truly, a friend in need is a friend indeed.",
                category = "Friendship"
            ),
            ProverbEntity(
                id = "proverb_14",
                englishText = "Don't put all your eggs in one basket.",
                hindiText = "सारा धन या जोखिम एक ही जगह मत लगाओ।",
                hindiEquivalent = "एक ही नाव में सारा भार मत रखो।",
                meaningEnglish = "Do not risk everything on a single venture or opportunity.",
                meaningHindi = "एक ही साधन पर पूरी तरह निर्भर रहना जोखिम भरा हो सकता है।",
                example = "Diversify your investments across stocks and savings; don't put all your eggs in one basket.",
                category = "Wisdom"
            ),
            ProverbEntity(
                id = "proverb_15",
                englishText = "Strike while the iron is hot.",
                hindiText = "अवसर का तुरंत लाभ उठाओ।",
                hindiEquivalent = "लोहा गर्म है, मार दो हथौड़ा।",
                meaningEnglish = "Take decisive action when the timing and circumstances are most favorable.",
                meaningHindi = "सही मौका मिलते ही तुरंत कदम उठाना चाहिए, देरी करने से अवसर हाथ से निकल जाता है।",
                example = "The client is very impressed right now—strike while the iron is hot and close the deal.",
                category = "Action"
            ),
            ProverbEntity(
                id = "proverb_16",
                englishText = "Too many cooks spoil the broth.",
                hindiText = "ज्यादा लोगों की दखलअंदाजी से काम बिगड़ जाता है।",
                hindiEquivalent = "बहुत जोगी मठ उजाड़।",
                meaningEnglish = "When too many people try to lead or manage a project, it turns into chaos.",
                meaningHindi = "जब किसी काम में बहुत से लोग अपनी-अपनी सलाह थोपते हैं, तो वह काम खराब हो जाता है।",
                example = "Assign only one lead for this presentation; too many cooks spoil the broth.",
                category = "Work"
            ),
            ProverbEntity(
                id = "proverb_17",
                englishText = "The pen is mightier than the sword.",
                hindiText = "कलम की ताकत तलवार से भी बड़ी होती है।",
                hindiEquivalent = "कलम में तलवार से ज्यादा धार होती है।",
                meaningEnglish = "Written words and communication have more lasting power and influence than physical violence.",
                meaningHindi = "विचारों और लेखन की शक्ति हिंसा और हथियारों से कहीं अधिक प्रभावशाली होती है।",
                example = "His investigative articles changed government policy—the pen is mightier than the sword.",
                category = "Wisdom"
            ),
            ProverbEntity(
                id = "proverb_18",
                englishText = "Necessity is the mother of invention.",
                hindiText = "आवश्यकता ही आविष्कार की जननी है।",
                hindiEquivalent = "गरज सब कुछ करा लेती है।",
                meaningEnglish = "When you truly need something, you find creative ways to create or solve it.",
                meaningHindi = "जब इंसान को किसी चीज़ की सख्त जरूरत होती है, तो वह नए-नए उपाय खोज निकालता है।",
                example = "Working remotely forced us to build digital tools—necessity is the mother of invention.",
                category = "Success"
            ),
            ProverbEntity(
                id = "proverb_19",
                englishText = "You reap what you sow.",
                hindiText = "जैसी करनी, वैसी भरनी।",
                hindiEquivalent = "जैसा बोओगे, वैसा काटोगे।",
                meaningEnglish = "Your future results and consequences will reflect your past efforts and actions.",
                meaningHindi = "इंसान जैसा कर्म करता है, उसे वैसा ही फल प्राप्त होता है।",
                example = "He worked hard all year and scored the top rank; you reap what you sow.",
                category = "Karma"
            ),
            ProverbEntity(
                id = "proverb_20",
                englishText = "Don't judge a book by its cover.",
                hindiText = "किसी को केवल उसके बाहरी रूप से मत आंको।",
                hindiEquivalent = "सूरत पर मत जाओ, सीरत देखो।",
                meaningEnglish = "Do not form an opinion about someone or something based purely on appearance.",
                meaningHindi = "बाहरी चमक-दमक देखकर किसी इंसान या वस्तु की योग्यता का फैसला नहीं करना चाहिए।",
                example = "The small restaurant looked simple from outside, but the food was world-class. Don't judge a book by its cover.",
                category = "Wisdom"
            ),
            ProverbEntity(
                id = "proverb_21",
                englishText = "Silence is golden.",
                hindiText = "चुप रहना कई बार सबसे बेहतर होता है।",
                hindiEquivalent = "एक चुप, सौ सुख।",
                meaningEnglish = "Keeping quiet is often wiser and more valuable than speaking unnecessarily.",
                meaningHindi = "व्यर्थ बहस में पड़ने से अच्छा है कि मौन रहकर शांति बनाए रखी जाए।",
                example = "Instead of arguing with an angry customer, he remained calm. Silence is golden.",
                category = "Mindset"
            ),
            ProverbEntity(
                id = "proverb_22",
                englishText = "Laughter is the best medicine.",
                hindiText = "हंसी सबसे अच्छी दवा है।",
                hindiEquivalent = "हंसना स्वास्थ्य के लिए वरदान है।",
                meaningEnglish = "Humor, joy, and positive thinking can alleviate stress and heal emotional pain.",
                meaningHindi = "हंसमुख रहने से मन और शरीर दोनों स्वस्थ रहते हैं और तनाव दूर होता है।",
                example = "Watching that comedy movie refreshed my mood completely; laughter is the best medicine.",
                category = "Health"
            ),
            ProverbEntity(
                id = "proverb_23",
                englishText = "Knowledge is power.",
                hindiText = "ज्ञान ही सबसे बड़ी शक्ति है।",
                hindiEquivalent = "विद्या ददाति विनयं (विद्या से शक्ति मिलती है)।",
                meaningEnglish = "The more knowledge and understanding you have, the more capable and empowered you become.",
                meaningHindi = "सही ज्ञान और समझ से इंसान हर मुश्किल पर विजय प्राप्त कर सकता है।",
                example = "Understanding contract law saved him millions; truly, knowledge is power.",
                category = "Wisdom"
            ),
            ProverbEntity(
                id = "proverb_24",
                englishText = "Haste makes waste.",
                hindiText = "जल्दबाजी का काम शैतान का होता है।",
                hindiEquivalent = "जल्दी का काम, खराबी का पैगाम।",
                meaningEnglish = "Rushing into things without proper attention often leads to mistakes and wasted effort.",
                meaningHindi = "उतावलेपन में काम करने से गलतियाँ होती हैं और समय व साधन दोनों बर्बाद होते हैं।",
                example = "Take your time while reviewing the code; haste makes waste.",
                category = "Patience"
            ),
            ProverbEntity(
                id = "proverb_25",
                englishText = "No pain, no gain.",
                hindiText = "बिना कष्ट सहे कुछ हासिल नहीं होता।",
                hindiEquivalent = "सेवा बिन मेवा नहीं।",
                meaningEnglish = "You cannot achieve success, fitness, or improvement without hard work and sacrifice.",
                meaningHindi = "बिना मेहनत और संघर्ष के कोई भी बड़ी उपलब्धि प्राप्त नहीं की जा सकती।",
                example = "Waking up at 5 AM for practice is tough, but no pain, no gain.",
                category = "Success"
            ),
            ProverbEntity(
                id = "proverb_26",
                englishText = "Fortune favors the bold.",
                hindiText = "किस्मत भी बहादुरों का साथ देती है।",
                hindiEquivalent = "हिम्मते मर्दां, मददे खुदा।",
                meaningEnglish = "Courageous people who take calculated risks are more likely to achieve great outcomes.",
                meaningHindi = "जो लोग साहस दिखाकर आगे बढ़ते हैं, सफलता उनके कदम चूमती है।",
                example = "He pitched his revolutionary idea to top investors and won funding—fortune favors the bold.",
                category = "Success"
            ),
            ProverbEntity(
                id = "proverb_27",
                englishText = "A bird in the hand is worth two in the bush.",
                hindiText = "जो पास है वह अधिक मूल्यवान है, उस चीज़ की तुलना में जो अनिश्चित है।",
                hindiEquivalent = "आधी छोड़ पूरी को धावे, आधी मिले न पूरी पावे।",
                meaningEnglish = "It is better to be content with what you have secured than risking it for uncertain gains.",
                meaningHindi = "हाथ में आई निश्चित वस्तु को छोड़कर अनिश्चित वस्तु के पीछे भागना मूर्खता है।",
                example = "Accept this confirmed job offer rather than waiting for an uncertain one; a bird in the hand is worth two in the bush.",
                category = "Wisdom"
            ),
            ProverbEntity(
                id = "proverb_28",
                englishText = "Two heads are better than one.",
                hindiText = "एक से भले दो।",
                hindiEquivalent = "एक और एक ग्यारह होते हैं।",
                meaningEnglish = "Two people working together can solve a problem more effectively than one person alone.",
                meaningHindi = "मिलकर काम करने से बेहतर विचार और त्वरित समाधान मिलते हैं।",
                example = "Let's brainstorm this solution together; two heads are better than one.",
                category = "Friendship"
            ),
            ProverbEntity(
                id = "proverb_29",
                englishText = "When in Rome, do as the Romans do.",
                hindiText = "जैसा देश, वैसा भेष।",
                hindiEquivalent = "जिस थाली में खाना, उसी का नियम निभाना।",
                meaningEnglish = "Adapt to the customs, culture, and etiquette of the place you are visiting or living in.",
                meaningHindi = "जहाँ रहें, वहाँ के तौर-तरीकों और संस्कृति का सम्मान करते हुए खुद को ढाल लेना चाहिए।",
                example = "When travelling in Japan, remember to remove your shoes indoors; when in Rome, do as the Romans do.",
                category = "Life"
            ),
            ProverbEntity(
                id = "proverb_30",
                englishText = "Cleanliness is next to godliness.",
                hindiText = "स्वच्छता में ही ईश्वर का वास होता है।",
                hindiEquivalent = "सफाई ईश्वर की भक्ति के समान है।",
                meaningEnglish = "Keeping yourself and your surroundings pure and clean is a moral and spiritual virtue.",
                meaningHindi = "तन और मन की स्वच्छता जीवन को शुद्ध और सकारात्मक बनाती है।",
                example = "Keep your study desk organized and tidy; cleanliness is next to godliness.",
                category = "Health"
            )
        )
    }
}
