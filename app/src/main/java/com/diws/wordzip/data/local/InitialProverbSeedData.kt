package com.diws.wordzip.data.local

object InitialProverbSeedData {
    fun getInitialProverbs(): List<ProverbEntity> {
        return listOf(
            ProverbEntity(
                id = "proverb_1",
                englishText = "Actions speak louder than words.",
                hindiText = "बातों से ज्यादा इंसान का काम बोलता है।",
                hindiEquivalent = "कथनी से करनी भली।",
                meaningEnglish = "A person's true character and intentions are demonstrated by their actions, not by their promises.",
                meaningHindi = "सिर्फ बड़ी बातें करने से कुछ नहीं होता; आपका आचरण ही असली चरित्र दिखाता है।",
                example = "Don't just promise you will help; actions speak louder than words.",
                category = "Action"
            ),
            ProverbEntity(
                id = "proverb_2",
                englishText = "A stitch in time saves nine.",
                hindiText = "समय पर किया गया छोटा सुधार भविष्य की बड़ी विपत्ति से बचाता है।",
                hindiEquivalent = "समय चूकि पुनि का पछिताने।",
                meaningEnglish = "Dealing with a minor issue promptly prevents it from turning into an overwhelming problem later.",
                meaningHindi = "शुरुआत में ही छोटी खामी ठीक कर लेने से बाद में भारी बर्बादी से बचा जा सकता है।",
                example = "Fix the software bug before release; remember, a stitch in time saves nine.",
                category = "Wisdom"
            ),
            ProverbEntity(
                id = "proverb_3",
                englishText = "Where there is a will, there is a way.",
                hindiText = "जहाँ दृढ़ संकल्प है, वहाँ मार्ग अवश्य मिलता है।",
                hindiEquivalent = "मन के हारे हार है, मन के जीते जीत।",
                meaningEnglish = "Unwavering resolve and determination can overcome even the most formidable obstacles.",
                meaningHindi = "यदि किसी लक्ष्य को पाने का अटूट इरादा हो, तो हर कठिन परिस्थिति से निकलने का रास्ता मिल जाता है।",
                example = "Despite financial hardships, she built a startup—where there is a will, there is a way.",
                category = "Success"
            ),
            ProverbEntity(
                id = "proverb_4",
                englishText = "All that glitters is not gold.",
                hindiText = "हर चमकती हुई वस्तु मूल्यवान नहीं होती।",
                hindiEquivalent = "हाथी के दांत खाने के और, दिखाने के और।",
                meaningEnglish = "Superficial charm or attractive appearances often disguise lack of true worth or hidden deceit.",
                meaningHindi = "बाहरी चमक-दमक पर कभी अंधा भरोसा नहीं करना चाहिए; आंतरिक वास्तविकता ही सच्ची होती है।",
                example = "The scheme promised doubling money in a month, but all that glitters is not gold.",
                category = "Wisdom"
            ),
            ProverbEntity(
                id = "proverb_5",
                englishText = "Better late than never.",
                hindiText = "देर से किया गया सही कार्य न करने से कहीं बेहतर है।",
                hindiEquivalent = "देर आए दुरुस्त आए।",
                meaningEnglish = "Completing a necessary duty belatedly is preferable to abandoning it entirely.",
                meaningHindi = "किसी आवश्यक काम को बिल्कुल न करने से लाख गुना बेहतर है कि उसे देर से ही सही, पूरा कर लिया जाए।",
                example = "He enrolled in university at the age of thirty-five; better late than never.",
                category = "Life"
            ),
            ProverbEntity(
                id = "proverb_6",
                englishText = "Every cloud has a silver lining.",
                hindiText = "हर घोर संकट के भीतर आशा की एक किरण छिपी होती है।",
                hindiEquivalent = "रात जितनी गहरी होती है, प्रभात उतना ही निकट होता है।",
                meaningEnglish = "Even the bleakest circumstances bring unforeseen opportunities or positive lessons.",
                meaningHindi = "हर बुरे समय और विपदा के पीछे कोई न कोई छिपी हुई भलाई अवश्य होती है।",
                example = "Losing that job forced him to innovate and start his venture—every cloud has a silver lining.",
                category = "Mindset"
            ),
            ProverbEntity(
                id = "proverb_7",
                englishText = "Discretion is the better part of valor.",
                hindiText = "बिना सोचे-समझे साहस दिखाने से सूझबूझ और विवेक से पीछे हटना बेहतर है।",
                hindiEquivalent = "सावधानी हटी, दुर्घटना घटी / समझदारी ही असली वीरता है।",
                meaningEnglish = "Exercising prudent caution and avoiding unnecessary risks is wiser than reckless bravado.",
                meaningHindi = "अनावश्यक खतरे में कूदने के बजाय विवेकपूर्ण ढंग से अपनी रक्षा करना ही वास्तविक बुद्धिमानी है।",
                example = "He chose to negotiate rather than escalate an unwinnable conflict; discretion is the better part of valor.",
                category = "Strategy"
            ),
            ProverbEntity(
                id = "proverb_8",
                englishText = "Empty vessels make the most noise.",
                hindiText = "अल्पज्ञानी व्यक्ति ही सबसे अधिक शेखी बघारते हैं।",
                hindiEquivalent = "अधजल गगरी छलकत जाए, भरी गगरिया चुपके जाए।",
                meaningEnglish = "People with the least knowledge, depth, or talent are typically the loudest and boast the most.",
                meaningHindi = "जिस व्यक्ति के पास कम ज्ञान या योग्यता होती है, वह दिखावा और शोर सबसे ज्यादा करता है।",
                example = "The self-proclaimed expert contributed nothing; truly, empty vessels make the most noise.",
                category = "Human Nature"
            ),
            ProverbEntity(
                id = "proverb_9",
                englishText = "Smooth seas do not make skillful sailors.",
                hindiText = "शांत समुद्र कभी किसी को कुशल नाविक नहीं बनाता।",
                hindiEquivalent = "विपत्ति ही पुरुष के धैर्य और सामर्थ्य की कसौटी है।",
                meaningEnglish = "True competence, resilience, and character are forged through hardship and rigorous adversity.",
                meaningHindi = "आसान परिस्थितियों में नहीं, बल्कि मुश्किलों और संघर्षों से जूझकर ही इंसान वास्तव में सक्षम बनता है।",
                example = "Facing market downturns taught the leadership team resilience; smooth seas do not make skillful sailors.",
                category = "Mindset"
            ),
            ProverbEntity(
                id = "proverb_10",
                englishText = "Still waters run deep.",
                hindiText = "शांत दिखने वाले व्यक्ति के भीतर गहरा ज्ञान और चिंतन होता है।",
                hindiEquivalent = "गंभीर नदी का जल शांत बहता है।",
                meaningEnglish = "Quiet, reserved people often possess profound intellect, passion, and hidden complexity.",
                meaningHindi = "जो लोग कम बोलते हैं और शांत रहते हैं, उनके भीतर अक्सर अपार गहराई, ज्ञान और प्रतिभा होती है।",
                example = "She rarely boasted in meetings, but her strategic plan was brilliant; still waters run deep.",
                category = "Philosophy"
            ),
            ProverbEntity(
                id = "proverb_11",
                englishText = "People who live in glass houses should not throw stones.",
                hindiText = "जिनमें स्वयं कमियां हों, उन्हें दूसरों पर लांछन नहीं लगाना चाहिए।",
                hindiEquivalent = "सूप बोले तो बोले, छलनी क्या बोले जिसमें बहत्तर छेद।",
                meaningEnglish = "One should not criticize or accuse others for faults they themselves are guilty of.",
                meaningHindi = "जब इंसान खुद कमजोरियों से भरा हो, तो उसे दूसरों की आलोचना करने का अधिकार नहीं होता।",
                example = "He criticized his peer for being late despite always oversleeping; glass houses shouldn't throw stones.",
                category = "Human Nature"
            ),
            ProverbEntity(
                id = "proverb_12",
                englishText = "The squeaky wheel gets the grease.",
                hindiText = "जो अपनी मांग मुखर होकर रखता है, उसी पर सबसे पहले ध्यान दिया जाता है।",
                hindiEquivalent = "बिन रोए तो मां भी दूध नहीं पिलाती।",
                meaningEnglish = "Those who persistently voice their concerns or demands receive the most attention and resources.",
                meaningHindi = "अपनी बात और अधिकार को स्पष्ट रूप से सामने रखने वाले व्यक्ति को ही प्राथमिकता मिलती है।",
                example = "Speak up in the planning meeting if your team needs more compute; the squeaky wheel gets the grease.",
                category = "Action"
            ),
            ProverbEntity(
                id = "proverb_13",
                englishText = "A rolling stone gathers no moss.",
                hindiText = "लगातार स्थान और लक्ष्य बदलने वाला व्यक्ति स्थिरता और उपलब्धि हासिल नहीं कर पाता।",
                hindiEquivalent = "धोबी का कुत्ता न घर का न घाट का / थाली का बैंगन।",
                meaningEnglish = "A person who constantly shifts jobs, places, or commitments avoids burdens but fails to build lasting roots.",
                meaningHindi = "जो व्यक्ति एक जगह टिककर काम नहीं करता, वह न अनुभव संचित कर पाता है और न ही प्रतिष्ठा।",
                example = "He changed five industries in three years without mastering any; a rolling stone gathers no moss.",
                category = "Life"
            ),
            ProverbEntity(
                id = "proverb_14",
                englishText = "Necessity is the mother of invention.",
                hindiText = "आवश्यकता ही नवीन आविष्कारों और उपायों को जन्म देती है।",
                hindiEquivalent = "गरज सब कुछ करा लेती है / मजबूरी नई राह दिखाती है।",
                meaningEnglish = "Urgent requirements and critical challenges drive people to devise ingenious and creative solutions.",
                meaningHindi = "जब मनुष्य के सामने कोई गंभीर चुनौती आती है, तब वह अपनी बुद्धि से नए रास्ते खोज निकालता है।",
                example = "Resource constraints pushed the team to build ultra-fast algorithms—necessity is the mother of invention.",
                category = "Wisdom"
            ),
            ProverbEntity(
                id = "proverb_15",
                englishText = "He who pays the piper calls the tune.",
                hindiText = "जो धन और संसाधन लगाता है, निर्णय लेने का अधिकार भी उसी का होता है।",
                hindiEquivalent = "जिसकी लाठी, उसकी भैंस।",
                meaningEnglish = "The person or entity funding an enterprise has the ultimate right to dictate how it is managed.",
                meaningHindi = "जो व्यक्ति पूंजी और आर्थिक समर्थन देता है, नीतियां और फैसले भी उसी के अनुसार तय होते हैं।",
                example = "Since the investor backed the entire round, they dictated company policy; he who pays the piper calls the tune.",
                category = "Strategy"
            ),
            ProverbEntity(
                id = "proverb_16",
                englishText = "The proof of the pudding is in the eating.",
                hindiText = "किसी भी सिद्धांत या वस्तु की वास्तविक गुणवत्ता उसके व्यावहारिक परिणाम से साबित होती है।",
                hindiEquivalent = "प्रत्यक्ष को प्रमाण की आवश्यकता नहीं होती।",
                meaningEnglish = "The real value and effectiveness of an idea or product can only be judged when put to practical test.",
                meaningHindi = "दावों और योजनाओं की असली परीक्षा तभी होती है जब उन्हें वास्तविक धरातल पर क्रियान्वित किया जाए।",
                example = "The pitch deck sounded magnificent, but the proof of the pudding was in the live user retention.",
                category = "Philosophy"
            ),
            ProverbEntity(
                id = "proverb_17",
                englishText = "Do not cast pearls before swine.",
                hindiText = "अयोग्य व्यक्ति के सामने अमूल्य ज्ञान या सलाह व्यर्थ मत करो।",
                hindiEquivalent = "बंदर क्या जाने अदरक का स्वाद / भैंस के आगे बीन बजाना।",
                meaningEnglish = "Offering valuable insights, art, or wisdom to those incapable of appreciating them is futile.",
                meaningHindi = "जो व्यक्ति ज्ञान और अच्छाई का मोल नहीं समझता, उसे अमूल्य विचार देना समय नष्ट करना है।",
                example = "Explaining fine design nuances to an arrogant critic is like casting pearls before swine.",
                category = "Wisdom"
            ),
            ProverbEntity(
                id = "proverb_18",
                englishText = "Among the blind, the one-eyed man is king.",
                hindiText = "अज्ञानियों के समाज में थोड़ा ज्ञान रखने वाला व्यक्ति भी बड़ा विद्वान मान लिया जाता है।",
                hindiEquivalent = "अंधों में काना राजा।",
                meaningEnglish = "An individual with modest abilities appears remarkably superior in the company of the totally incompetent.",
                meaningHindi = "जहाँ सभी लोग अयोग्य हों, वहाँ सामान्य समझ वाला व्यक्ति भी सर्वेसर्वा बन बैठता है।",
                example = "He only knew basic formulas, yet ran the team; among the blind, the one-eyed man is king.",
                category = "Human Nature"
            ),
            ProverbEntity(
                id = "proverb_19",
                englishText = "Pride goes before a fall.",
                hindiText = "अत्यधिक अहंकार और घमंड ही मनुष्य के पतन का मूल कारण बनता है।",
                hindiEquivalent = "घमंडी का सिर हमेशा नीचा होता है / रावण का भी घमंड नहीं रहा।",
                meaningEnglish = "Overconfidence and hubris blind a person to their vulnerabilities, inevitably precipitating their downfall.",
                meaningHindi = "जब इंसान अपनी सफलता के अहंकार में चूर हो जाता है, तब उसकी सतर्कता समाप्त हो जाती है।",
                example = "Dismissing all competitors led to the monopoly's downfall; pride goes before a fall.",
                category = "Mindset"
            ),
            ProverbEntity(
                id = "proverb_20",
                englishText = "Never look a gift horse in the mouth.",
                hindiText = "उपहार या बिना प्रयास मिली वस्तु में नुक्ताचीनी या दोष नहीं निकालना चाहिए।",
                hindiEquivalent = "दान की बछिया के दांत नहीं देखे जाते।",
                meaningEnglish = "Do not ungratefully criticize the flaws in something that was given to you gratuitously.",
                meaningHindi = "जब कोई निस्वार्थ भाव से सहायता या भेंट दे, तो उसमें कमियां निकालना कृतघ्नता है।",
                example = "They gave you a free upgrade for the trip; never look a gift horse in the mouth.",
                category = "Wisdom"
            ),
            ProverbEntity(
                id = "proverb_21",
                englishText = "A bird in the hand is worth two in the bush.",
                hindiText = "प्राप्त निश्चित वस्तु उस अनिश्चित वस्तु से श्रेष्ठ है जो भविष्य के भरोसे हो।",
                hindiEquivalent = "आधी छोड़ पूरी को धावे, आधी मिले न पूरी पावे।",
                meaningEnglish = "It is wiser to hold onto an assured advantage than to gamble it away in pursuit of elusive prospects.",
                meaningHindi = "जो हाथ में है उसे सुरक्षित रखना चाहिए; ज्यादा के लालच में अनिश्चितता के पीछे भागना घातक होता है।",
                example = "Lock in this confirmed contract; a bird in the hand is worth two in the bush.",
                category = "Strategy"
            ),
            ProverbEntity(
                id = "proverb_22",
                englishText = "Strike while the iron is hot.",
                hindiText = "अनुकूल अवसर आते ही बिना विलंब किए निर्णायक प्रहार करो।",
                hindiEquivalent = "लोहा गर्म है, मार दो हथौड़ा।",
                meaningEnglish = "Seize the most auspicious moment to act decisively before the opportune window closes.",
                meaningHindi = "जब परिस्थितियां पूरी तरह आपके पक्ष में हों, तब तुरंत कार्यवाही करनी चाहिए।",
                example = "Traffic is spiking from viral mentions—strike while the iron is hot and launch subscriptions.",
                category = "Action"
            ),
            ProverbEntity(
                id = "proverb_23",
                englishText = "You can catch more flies with honey than with vinegar.",
                hindiText = "कठोरता के बजाय मधुर व्यवहार और विनम्रता से लोगों को आसानी से प्रभावित किया जा सकता है।",
                hindiEquivalent = "मीठी वाणी बोलिए, मन का आपा खोय।",
                meaningEnglish = "Politeness, diplomacy, and gentleness are far more persuasive than hostility or aggression.",
                meaningHindi = "क्रोध और कटु वचनों से शत्रु बनते हैं, जबकि मिठास और आदर से विरोधी भी सहयोगी बन जाते हैं।",
                example = "Instead of arguing with the vendor, negotiate politely; honey catches more flies than vinegar.",
                category = "Wisdom"
            ),
            ProverbEntity(
                id = "proverb_24",
                englishText = "The road to hell is paved with good intentions.",
                hindiText = "केवल नेक इरादे काफी नहीं होते; यदि समझदारी न हो तो भारी विनाश हो सकता है।",
                hindiEquivalent = "नेक नीयत से की गई अदूरदर्शिता भी विनाशकारी होती है।",
                meaningEnglish = "Benevolent intentions, when executed carelessly or without foresight, can yield disastrous outcomes.",
                meaningHindi = "मन में अच्छी भावना होना ही पर्याप्त नहीं है; योजना की समझदारी और प्रभाव का आकलन अनिवार्य है।",
                example = "Micro-managing to protect the junior staff ended up paralyzing progress—the road to hell is paved with good intentions.",
                category = "Philosophy"
            ),
            ProverbEntity(
                id = "proverb_25",
                englishText = "A chain is only as strong as its weakest link.",
                hindiText = "किसी संगठन या प्रणाली की कुल शक्ति उसके सबसे कमजोर अंग पर निर्भर करती है।",
                hindiEquivalent = "एक कमजोर कड़ी पूरी जंजीर को तोड़ देती है।",
                meaningEnglish = "The overall reliability of an entire system or group is constrained by its most vulnerable component.",
                meaningHindi = "पूरी टीम कितनी भी सक्षम क्यों न हो, यदि एक हिस्सा कमजोर है तो पूरा प्रयास विफल हो सकता है।",
                example = "Even with heavy encryption, one weak password breached the server; a chain is only as strong as its weakest link.",
                category = "Strategy"
            ),
            ProverbEntity(
                id = "proverb_26",
                englishText = "You cannot make an omelet without breaking eggs.",
                hindiText = "किसी महान कार्य को सिद्ध करने के लिए कुछ त्याग और असुविधाएं झेलनी ही पड़ती हैं।",
                hindiEquivalent = "बिना सेवा मेवा नहीं / कुछ पाने के लिए कुछ खोना पड़ता है।",
                meaningEnglish = "Achieving transformative progress inevitably requires sacrifices, disruptions, or uncomfortable trade-offs.",
                meaningHindi = "किसी बड़े परिवर्तन के लिए पुराने ढर्रे को तोड़ना और कठिन निर्णय लेना अनिवार्य होता है।",
                example = "Refactoring the architecture caused temporary downtime, but you can't make an omelet without breaking eggs.",
                category = "Action"
            ),
            ProverbEntity(
                id = "proverb_27",
                englishText = "When the wind of change blows, some build walls, others build windmills.",
                hindiText = "जब परिवर्तन की आंधी चलती है, तब कायर अवरोध बनाते हैं जबकि बुद्धिमान उस शक्ति से ऊर्जा पैदा करते हैं।",
                hindiEquivalent = "विपत्ति को अवसर में बदलना ही बुद्धिमत्ता है।",
                meaningEnglish = "Visionaries adapt to revolutionary shifts and leverage them as catalysts, while conservatives resist.",
                meaningHindi = "बदलते समय से घबराकर भागने के बजाय नए अवसरों को पहचानकर प्रगति का मार्ग बनाना ही दूरदर्शिता है।",
                example = "While others panicked over AI, they integrated LLMs—building windmills instead of walls.",
                category = "Mindset"
            ),
            ProverbEntity(
                id = "proverb_28",
                englishText = "A watched pot never boils.",
                hindiText = "बेसब्री से किसी परिणाम का इंतजार करने पर समय बहुत लंबा और कष्टदायी प्रतीत होता है।",
                hindiEquivalent = "अधीरता से समय नहीं बीतता।",
                meaningEnglish = "Obsessively waiting and fixating on an anticipated event makes the passage of time feel agonizingly slow.",
                meaningHindi = "किसी प्रक्रिया पर लगातार टकटकी लगाए रखने से वह तेज नहीं होती, केवल बेचैनी बढ़ती है।",
                example = "Stop refreshing the build pipeline every second; a watched pot never boils.",
                category = "Patience"
            ),
            ProverbEntity(
                id = "proverb_29",
                englishText = "The darkest hour is just before the dawn.",
                hindiText = "सफलता और प्रकाश का आगमन घोर निराशा और अंधकार के ठीक बाद होता है।",
                hindiEquivalent = "दुख के बाद ही सुख का सवेरा होता है।",
                meaningEnglish = "Circumstances often appear at their most unbearable right before an imminent breakthrough or relief.",
                meaningHindi = "जब संकट अपने चरम पर हो, तभी समझ लेना चाहिए कि समाधान और नवप्रभात अत्यंत निकट है।",
                example = "Just when bankruptcy seemed certain, the enterprise contract closed—the darkest hour is just before the dawn.",
                category = "Mindset"
            ),
            ProverbEntity(
                id = "proverb_30",
                englishText = "Give a man a fish and you feed him for a day; teach a man to fish and you feed him for a lifetime.",
                hindiText = "किसी की तात्कालिक सहायता करने से कहीं श्रेष्ठ है उसे आत्मनिर्भर और हुनरमंद बनाना।",
                hindiEquivalent = "बैसाखी देने से अच्छा है चलना सिखाना।",
                meaningEnglish = "Imparting fundamental skills and self-sufficiency creates lasting empowerment far exceeding transient charity.",
                meaningHindi = "किसी को बनी-बनाई मदद देने के बजाय हुनर सिखा देने से वह आजीवन स्वाभिमान से जी सकता है।",
                example = "Instead of writing their code, mentor them through debugging; teach a man to fish and feed him for life.",
                category = "Wisdom"
            ),
            ProverbEntity(
                id = "proverb_31",
                englishText = "An apple a day keeps the doctor away.",
                hindiText = "नियमित रूप से पौष्टिक और संतुलित आहार लेने से बीमारियां दूर रहती हैं।",
                hindiEquivalent = "स्वस्थ आहार, निरोगी काया।",
                meaningEnglish = "Consuming wholesome nourishment regularly maintains vitality and averts illness.",
                meaningHindi = "प्रतिदिन अपनी सेहत और खान-पान का ध्यान रखने से औषधियों की आवश्यकता नहीं पड़ती।",
                example = "Prioritize your sleep, hydration, and nutrition; an apple a day keeps the doctor away.",
                category = "Health"
            ),
            ProverbEntity(
                id = "proverb_32",
                englishText = "A journey of a thousand miles begins with a single step.",
                hindiText = "हजारों मील की अनंत यात्रा भी पहले छोटे कदम से ही प्रारंभ होती है।",
                hindiEquivalent = "बड़ी मंजिल की शुरुआत पहले कदम से होती है।",
                meaningEnglish = "Even the most monumental undertakings commence with one humble, decisive action.",
                meaningHindi = "कितना भी विशाल लक्ष्य क्यों न हो, जब तक आप पहला कदम नहीं उठाएंगे, तब तक यात्रा पूरी नहीं हो सकती।",
                example = "Write your first test today—a journey of a thousand miles begins with a single step.",
                category = "Action"
            ),
            ProverbEntity(
                id = "proverb_33",
                englishText = "Don't count your chickens before they hatch.",
                hindiText = "कार्य के वास्तविक संपन्न होने से पूर्व ही मुनाफे का उत्सव मत मनाओ।",
                hindiEquivalent = "हवा में महल मत बनाओ / आधी छोड़ सारी को धावे।",
                meaningEnglish = "Do not base future plans on assumed successes before they have tangibly materialized.",
                meaningHindi = "जब तक सफलता पूरी तरह हाथ में न आ जाए, तब तक उसकी कल्पना करके अति-उत्साहित नहीं होना चाहिए।",
                example = "Wait until the agreement is signed before spending budget; don't count chickens before they hatch.",
                category = "Wisdom"
            ),
            ProverbEntity(
                id = "proverb_34",
                englishText = "Once bitten, twice shy.",
                hindiText = "एक बार धोखा खा चुका व्यक्ति भविष्य में अत्यंत सावधान और सतर्क हो जाता है।",
                hindiEquivalent = "दूध का जला छाछ भी फूंक-फूंक कर पीता है।",
                meaningEnglish = "An unpleasant experience induces heightened vigilance and circumspection in similar situations.",
                meaningHindi = "जिस व्यक्ति को किसी बात में गहरा नुकसान हुआ हो, वह छोटी बातों में भी पूरी सावधानी बरतता है।",
                example = "After suffering a data leak last year, the company audits everything—once bitten, twice shy.",
                category = "Human Nature"
            ),
            ProverbEntity(
                id = "proverb_35",
                englishText = "The pen is mightier than the sword.",
                hindiText = "कलम और विचारों का प्रभाव हथियारों और शारीरिक बल से कहीं अधिक दीर्घकालिक होता है।",
                hindiEquivalent = "कलम में तलवार से ज्यादा धार होती है।",
                meaningEnglish = "Literary eloquence, ideas, and communication exert far greater civilizational power than brute military force.",
                meaningHindi = "हथियारों से केवल भय उत्पन्न किया जा सकता है, किंतु विचारों से मानव चेतना को बदला जा सकता है।",
                example = "His investigative journalism altered government policy—the pen is mightier than the sword.",
                category = "Philosophy"
            ),
            ProverbEntity(
                id = "proverb_36",
                englishText = "Too many cooks spoil the broth.",
                hindiText = "जब एक ही कार्य में बहुत से लोग अनियंत्रित हस्तक्षेप करते हैं, तो काम बिगड़ जाता है।",
                hindiEquivalent = "बहुत जोगी मठ उजाड़ / ज्यादा नाई बाल खराब करते हैं।",
                meaningEnglish = "When excessive stakeholders meddle with conflicting directives, chaos and poor execution result.",
                meaningHindi = "किसी परियोजना में जब बहुत सारे लोग अपनी-अपनी राय थोपते हैं, तो अंतिम परिणाम नष्ट हो जाता है।",
                example = "Assigning four managers to one small feature created chaos; too many cooks spoil the broth.",
                category = "Strategy"
            ),
            ProverbEntity(
                id = "proverb_37",
                englishText = "When in Rome, do as the Romans do.",
                hindiText = "जहाँ रहो, वहाँ की संस्कृति, रीति-रिवाजों और मर्यादाओं का सम्मान करते हुए खुद को ढालो।",
                hindiEquivalent = "जैसा देश, वैसा भेष।",
                meaningEnglish = "Adapt your behavior, etiquette, and practices to align respectfully with local customs.",
                meaningHindi = "किसी नए स्थान या परिवेश में जाने पर वहाँ के नियमों और शिष्टाचार का पालन करना ही समझदारी है।",
                example = "Follow the team's established code formatting; when in Rome, do as the Romans do.",
                category = "Wisdom"
            ),
            ProverbEntity(
                id = "proverb_38",
                englishText = "Barking dogs seldom bite.",
                hindiText = "जो लोग केवल डींगें हांकते और डराते हैं, वे वास्तव में कोई ठोस कार्यवाही नहीं कर पाते।",
                hindiEquivalent = "जो गरजते हैं, वो बरसते नहीं।",
                meaningEnglish = "Individuals who utter aggressive threats or bluster rarely execute genuine harm.",
                meaningHindi = "खोखला शोर मचाने वाले और धमकी देने वाले लोगों में वास्तविक साहस का अभाव होता है।",
                example = "Ignore their hostile threats; barking dogs seldom bite.",
                category = "Human Nature"
            ),
            ProverbEntity(
                id = "proverb_39",
                englishText = "A bad workman always blames his tools.",
                hindiText = "अयोग्य व्यक्ति अपनी असफलता का दोष अपने उपकरणों या परिस्थितियों पर मढ़ता है।",
                hindiEquivalent = "नाच न जाने आंगन टेढ़ा।",
                meaningEnglish = "Incompetent practitioners rationalize their poor performance by finding fault with their equipment.",
                meaningHindi = "जिसमें स्वयं हुनर की कमी होती है, वह अपनी गलती स्वीकारने के बजाय साधनों की बुराई करता है।",
                example = "He blamed the text editor for compilation errors, but a bad workman always blames his tools.",
                category = "Human Nature"
            ),
            ProverbEntity(
                id = "proverb_40",
                englishText = "Curiosity killed the cat, but satisfaction brought it back.",
                hindiText = "अनावश्यक उत्सुकता संकट में डाल सकती है, किंतु ज्ञान की खोज संतोष भी दिलाती है।",
                hindiEquivalent = "जिज्ञासा ज्ञान का द्वार है, पर बिना सोचे कूदना जोखिम भरा है।",
                meaningEnglish = "While reckless probing into hazardous matters invites danger, purposeful inquiry brings ultimate enlightenment.",
                meaningHindi = "व्यर्थ के मामलों में ताक-झांक हानिकारक होती है, लेकिन गहरी शोध से सत्य का साक्षात्कार होता है।",
                example = "He spent all night exploring the esoteric bug; curiosity cost sleep, but solving it brought satisfaction.",
                category = "Philosophy"
            ),
            ProverbEntity(
                id = "proverb_41",
                englishText = "Fools rush in where angels fear to tread.",
                hindiText = "मूर्ख और अज्ञानी व्यक्ति बिना सोचे-समझे उस जोखिम में कूद पड़ते हैं जिससे ज्ञानी भी बचते हैं।",
                hindiEquivalent = "अंधा गाये बहरा बजाये / अक्ल के अंधे, गांठ के पूरे।",
                meaningEnglish = "Reckless, ignorant novices hastily venture into hazardous situations where seasoned experts exercise caution.",
                meaningHindi = "जहाँ अनुभवी लोग पूरी सावधानी के साथ कदम रखते हैं, वहाँ नासमझ बिना सोचे छलांग लगा देते हैं।",
                example = "Deploying breaking schema changes to production without backup is foolish; fools rush in where angels fear to tread.",
                category = "Wisdom"
            ),
            ProverbEntity(
                id = "proverb_42",
                englishText = "Opportunity seldom knocks twice.",
                hindiText = "स्वर्ण अवसर जीवन में बार-बार द्वार नहीं खटखटाते।",
                hindiEquivalent = "समय का पहिया हाथ नहीं आता।",
                meaningEnglish = "Favorable circumstances and life-defining chances are rare and must be seized promptly.",
                meaningHindi = "भाग्य जब उत्तम अवसर प्रदान करे, तो तुरंत लाभ उठाना चाहिए, क्योंकि खोया मौका दोबारा नहीं मिलता।",
                example = "Accept the fellowship invitation; opportunity seldom knocks twice.",
                category = "Success"
            ),
            ProverbEntity(
                id = "proverb_43",
                englishText = "Ignorance of the law excuses no one.",
                hindiText = "कानून या नियमों की अनभिज्ञता किसी को दंड से मुक्त नहीं करती।",
                hindiEquivalent = "नियम का ज्ञान न होना अपराध से मुक्ति का आधार नहीं।",
                meaningEnglish = "Being unaware of a statute or regulation does not exonerate an individual from liability.",
                meaningHindi = "यदि आप नियमों से अनजान हैं, तब भी उल्लंघन होने पर उत्तरदायित्व आपका ही होगा।",
                example = "Failing to comply with privacy laws brought heavy penalties; ignorance of the law excuses no one.",
                category = "Strategy"
            ),
            ProverbEntity(
                id = "proverb_44",
                englishText = "A guilty conscience needs no accuser.",
                hindiText = "जिसने गलत किया हो, उसका अंतर्मन ही उसे निरंतर भयभीत और बेचैन रखता है।",
                hindiEquivalent = "चोर की दाढ़ी में तिनका / सांच को आंच नहीं।",
                meaningEnglish = "One burdened with remorse constantly anticipates exposure and condemns oneself from within.",
                meaningHindi = "अपराध करने वाले का मन हमेशा आशंकित रहता है और वह बिना किसी के टोके ही घबरा जाता है।",
                example = "He nervously justified his audit logs before anyone even questioned him; a guilty conscience needs no accuser.",
                category = "Human Nature"
            ),
            ProverbEntity(
                id = "proverb_45",
                englishText = "Experience is the father of wisdom.",
                hindiText = "व्यावहारिक अनुभव और संघर्षों से ही सच्ची प्रज्ञा और बुद्धिमत्ता का जन्म होता है।",
                hindiEquivalent = "बादाम खाने से नहीं, ठोकर खाने से अक्ल आती है।",
                meaningEnglish = "Living through trials, triumphs, and failures imparts deeper insights than mere theoretical knowledge.",
                meaningHindi = "केवल पुस्तकें पढ़ने से नहीं, बल्कि जीवन के वास्तविक थपेड़े सहने पर ही ज्ञान परिपक्व होता है।",
                example = "His calm reaction during the severe outage came from twenty years of experience; experience is the father of wisdom.",
                category = "Wisdom"
            ),
            ProverbEntity(
                id = "proverb_46",
                englishText = "You cannot see the wood for the trees.",
                hindiText = "छोटी-छोटी बातों में इतना उलझ जाना कि मुख्य उद्देश्य या समग्र तस्वीर दिखाई न दे।",
                hindiEquivalent = "राई का पहाड़ बनाना / बाल की खाल निकालना।",
                meaningEnglish = "Fixating so excessively on minute details that one loses sight of the overarching objective.",
                meaningHindi = "बारीकियों में इतना खो जाना कि मुख्य योजना और व्यापक दृष्टिकोण ओझल हो जाए।",
                example = "Don't spend days debating button radius while authentication is broken; don't lose the wood for the trees.",
                category = "Strategy"
            ),
            ProverbEntity(
                id = "proverb_47",
                englishText = "Hope is a good breakfast, but it is a bad supper.",
                hindiText = "आशा से दिन की शुरुआत तो अच्छी हो सकती है, किंतु केवल आशा के भरोसे रात का पेट नहीं भरता।",
                hindiEquivalent = "खयाली पुलाव पकाने से भूख नहीं मिटती।",
                meaningEnglish = "Optimism provides initial enthusiasm, but concrete action and tangible results are indispensable for survival.",
                meaningHindi = "सकारात्मक सोचना अच्छा है, लेकिन यदि ठोस कर्म न किए जाएं तो केवल उम्मीदों से परिणाम हासिल नहीं होते।",
                example = "Relying purely on organic virality without a monetization plan is dangerous—hope is a good breakfast, but a bad supper.",
                category = "Philosophy"
            ),
            ProverbEntity(
                id = "proverb_48",
                englishText = "Better to light a candle than to curse the darkness.",
                hindiText = "विपत्ति या बुराई को कोसते रहने के बजाय समाधान के लिए एक छोटा सा प्रयास करना श्रेयस्कर है।",
                hindiEquivalent = "अंधेरे को कोसने से अच्छा है एक दीपक जलाना।",
                meaningEnglish = "Taking even the smallest constructive action is far more productive than chronic, passive complaining.",
                meaningHindi = "समस्याओं का रोना रोने के बजाय स्थिति को सुधारने के लिए अपनी क्षमतानुसार कदम उठाना ही असली बुद्धिमानी है।",
                example = "Instead of complaining about lack of documentation, she wrote a starter tutorial—better to light a candle than curse darkness.",
                category = "Mindset"
            ),
            ProverbEntity(
                id = "proverb_49",
                englishText = "Deep rivers move in silence, shallow brooks are noisy.",
                hindiText = "गंभीर और विद्वान लोग शांत रहते हैं, जबकि उथले ज्ञान वाले व्यर्थ का कोलाहल करते हैं।",
                hindiEquivalent = "गंभीर नदी का शांत बहाव / थोथा चना बाजे घना।",
                meaningEnglish = "Profound mastery operates with calm composure, whereas superficial minds boast noisily.",
                meaningHindi = "जिसके पास वास्तविक सामर्थ्य और ज्ञान होता है वह विनम्र और मौन रहता है, जबकि अज्ञानी सदा शोर मचाता है।",
                example = "The lead architect spoke rarely, but each insight restructured the project; deep rivers move in silence.",
                category = "Philosophy"
            ),
            ProverbEntity(
                id = "proverb_50",
                englishText = "A ship in harbor is safe, but that is not what ships are built for.",
                hindiText = "सुरक्षित दायरे में रहना आसान है, परंतु महान उपलब्धियां जोखिम उठाने और आगे बढ़ने से ही मिलती हैं।",
                hindiEquivalent = "किनारे पर बैठकर मोती नहीं मिलते, गहरे पानी में उतरना पड़ता है।",
                meaningEnglish = "Remaining in one's comfort zone guarantees safety but stifles growth, discovery, and great achievements.",
                meaningHindi = "सुख-सुविधा में बंधे रहने से विकास रुक जाता है; जीवन का वास्तविक उद्देश्य चुनौतियों का सामना करना है।",
                example = "Leaving a stable job to venture into quantum computing was daunting, but a ship in harbor is not what ships are built for.",
                category = "Mindset"
            ),
            ProverbEntity(
                id = "proverb_51",
                englishText = "Never judge a horse by its saddle.",
                hindiText = "किसी की योग्यता का अनुमान उसके बाहरी ठाठ-बाट या वेशभूषा से मत लगाओ।",
                hindiEquivalent = "सूरत पर मत जाओ, सीरत देखो।",
                meaningEnglish = "Outward embellishments and accessories do not determine underlying power, endurance, or substance.",
                meaningHindi = "दिखावे और चमक-दमक के आधार पर किसी व्यक्ति की आंतरिक प्रतिभा का मूल्यांकन नहीं करना चाहिए।",
                example = "His presentation was plain, but the core engine architecture was bulletproof; never judge a horse by its saddle.",
                category = "Human Nature"
            ),
            ProverbEntity(
                id = "proverb_52",
                englishText = "An ounce of prevention is worth a pound of cure.",
                hindiText = "बीमारी या संकट के बाद इलाज कराने से लाख गुना बेहतर है कि पहले से ही बचाव किया जाए।",
                hindiEquivalent = "इलाज से परहेज बेहतर है।",
                meaningEnglish = "Investing modest effort in preventative measures preempts enormous expenditures and anguish later.",
                meaningHindi = "मुसीबत आने के बाद उपाय ढूंढने के बजाय पहले से ही सतर्कता बरतना कहीं अधिक बुद्धिमानी है।",
                example = "Automated regression testing in pipeline saves weeks of post-launch fire fighting—an ounce of prevention is worth a pound of cure.",
                category = "Wisdom"
            ),
            ProverbEntity(
                id = "proverb_53",
                englishText = "He who sups with the devil should have a long spoon.",
                hindiText = "कुटिल और धोखेबाज लोगों से संबंध रखते समय अत्यधिक सतर्कता और दूरी बनाए रखनी चाहिए।",
                hindiEquivalent = "सांप से दोस्ती करो तो लाठी हाथ में रखो।",
                meaningEnglish = "If you must associate or negotiate with deceitful individuals, protect yourself with strict safeguards.",
                meaningHindi = "दुष्ट और अविश्वसनीय व्यक्तियों के साथ व्यवहार करते समय पूरी सावधानी और सुरक्षा रखना अनिवार्य है।",
                example = "When negotiating with unprincipled competitors, insist on escrow milestones; he who sups with the devil needs a long spoon.",
                category = "Strategy"
            ),
            ProverbEntity(
                id = "proverb_54",
                englishText = "There is no smoke without fire.",
                hindiText = "बिना किसी आधार या सच्चाई के कोई भी अफवाह या चर्चा नहीं फैलती।",
                hindiEquivalent = "बिना चिंगारी के धुआं नहीं उठता।",
                meaningEnglish = "Persistent rumors or warning signs usually indicate at least a kernel of underlying truth.",
                meaningHindi = "यदि किसी विषय में लगातार बातें हो रही हैं, तो उसके पीछे कुछ न कुछ वास्तविक कारण अवश्य होता है।",
                example = "Multiple analysts raised accounting irregularities; remember, there is no smoke without fire.",
                category = "Human Nature"
            ),
            ProverbEntity(
                id = "proverb_55",
                englishText = "Time and tide wait for no man.",
                hindiText = "समय और अवसर की धारा किसी की प्रतीक्षा नहीं करती।",
                hindiEquivalent = "गया वक्त फिर हाथ नहीं आता।",
                meaningEnglish = "Temporal flow and inevitable forces of nature progress relentlessly regardless of human hesitations.",
                meaningHindi = "समय निरंतर बीत रहा है; जो आलस्य में डूबे रहते हैं, वे अवसर गवां बैठते हैं।",
                example = "Submit the patent application before the symposium; time and tide wait for no man.",
                category = "Action"
            ),
            ProverbEntity(
                id = "proverb_56",
                englishText = "You can't judge a fish by its ability to climb a tree.",
                hindiText = "हर प्राणी की विशिष्ट प्रतिभा होती है; अनुचित पैमाने से किसी की योग्यता नहीं आंकी जा सकती।",
                hindiEquivalent = "हर किसी का अपना हुनर होता है, सबको एक तराजू में मत तौलो।",
                meaningEnglish = "Judging individuals on criteria contrary to their innate genius leads to unjust condemnation of their worth.",
                meaningHindi = "हर व्यक्ति की अपनी अद्वितीय क्षमताएं होती हैं; गलत कसौटी पर परखने से प्रतिभाशाली भी असमर्थ लग सकता है।",
                example = "He struggled with stage speeches but wrote world-class compiler passes; you can't judge a fish by its climbing ability.",
                category = "Philosophy"
            ),
            ProverbEntity(
                id = "proverb_57",
                englishText = "Adversity and loss make a man wise.",
                hindiText = "कठिनाइयां और असफलताएं ही इंसान को परिपक्व और समझदार बनाती हैं।",
                hindiEquivalent = "कष्ट सहे बिना कोई ज्ञानी नहीं बनता / ठोकरें ही चलना सिखाती हैं।",
                meaningEnglish = "Suffering hardship and navigating setbacks instill profound prudence and psychological maturity.",
                meaningHindi = "जीवन की कठिन चुनौतियां और नुकसान ही मनुष्य को सच्ची समझ और विवेक प्रदान करते हैं।",
                example = "Rebounding from the product cancellation taught the founders invaluable lessons; adversity makes a man wise.",
                category = "Mindset"
            ),
            ProverbEntity(
                id = "proverb_58",
                englishText = "Rome wasn't built in a day, but they worked on it every day.",
                hindiText = "महान उपलब्धियों के निर्माण में निरंतर और अविराम परिश्रम लगता है।",
                hindiEquivalent = "हथेली पर सरसों नहीं जमती, पर रोज सींचना पड़ता है।",
                meaningEnglish = "Monumental masterpieces demand persistent, disciplined daily contribution rather than overnight miracle.",
                meaningHindi = "बड़ी सफलताएं रातों-रात नहीं मिलतीं, उनके पीछे वर्षों का अटूट और सतत प्रयास होता है।",
                example = "Stay dedicated to daily language practice; Rome wasn't built in a day.",
                category = "Patience"
            ),
            ProverbEntity(
                id = "proverb_59",
                englishText = "Knowledge talks, wisdom listens.",
                hindiText = "ज्ञान बोलने और जताने में उत्सुक रहता है, जबकि प्रज्ञा और विवेक मौन रहकर सुनता है।",
                hindiEquivalent = "ज्ञानी सुनता है, अज्ञानी बड़बड़ाता है।",
                meaningEnglish = "Acquired knowledge seeks to assert itself, whereas mature wisdom prioritizes attentive observation.",
                meaningHindi = "अल्पज्ञानी अपनी बातें मनवाने में लगा रहता है, जबकि प्रज्ञावान व्यक्ति दूसरों को सुनकर सत्य को ग्रहण करता है।",
                example = "During the heated architecture review, the principal fellow listened quietly before synthesizing the fix; knowledge talks, wisdom listens.",
                category = "Wisdom"
            ),
            ProverbEntity(
                id = "proverb_60",
                englishText = "Beauty is in the eye of the beholder.",
                hindiText = "सौंदर्य और मूल्य का आकलन देखने वाले के दृष्टिकोण पर निर्भर करता है।",
                hindiEquivalent = "जाकी रही भावना जैसी, प्रभु मूरत देखी तिन तैसी।",
                meaningEnglish = "Aesthetic appreciation and value judgments are purely subjective to individual perception.",
                meaningHindi = "किसी वस्तु या विचार की सुंदरता देखने वाले की दृष्टि और भावना में होती है।",
                example = "Some found the minimalist terminal aesthetic austere, while others called it sublime; beauty is in the eye of the beholder.",
                category = "Philosophy"
            ),
            ProverbEntity(
                id = "proverb_61",
                englishText = "The shoe that fits one person pinches another.",
                hindiText = "जो उपाय या नियम एक के लिए उपयुक्त है, वह आवश्यक नहीं कि दूसरे के लिए भी सही हो।",
                hindiEquivalent = "सबके लिए एक ही दवा काम नहीं करती / अपनी-अपनी ढपली, अपना-अपना राग।",
                meaningEnglish = "There is no universally optimal formula for life; strategies must be tailored to specific individual needs.",
                meaningHindi = "प्रत्येक मनुष्य की प्रकृति भिन्न होती है, इसलिए एक ही समाधान सब पर लागू नहीं किया जा सकता।",
                example = "Strict synchronous standups helped one team but choked another; the shoe that fits one pinches another.",
                category = "Life"
            ),
            ProverbEntity(
                id = "proverb_62",
                englishText = "Easy come, easy go.",
                hindiText = "बिना कठिन परिश्रम के सरलता से मिला धन या यश उतनी ही शीघ्रता से नष्ट भी हो जाता है।",
                hindiEquivalent = "चोरी का माल मोरी में / हराम की कमाई हवा में उड़ती है।",
                meaningEnglish = "Wealth, acclaim, or advantages acquired effortlessly are squandered just as swiftly without appreciation.",
                meaningHindi = "जो वस्तु बिना मेहनत के मिल जाती है, उसका सम्मान नहीं होता और वह जल्द ही समाप्त हो जाती है।",
                example = "He squandered his instant windfall within months—easy come, easy go.",
                category = "Human Nature"
            ),
            ProverbEntity(
                id = "proverb_63",
                englishText = "Great minds think alike, but fools rarely differ.",
                hindiText = "बुद्धिमान लोग सत्य की दिशा में समान विचार रखते हैं, परंतु मूर्ख भी अंधानुकरण में एक जैसे होते हैं।",
                hindiEquivalent = "विद्वानों का मत एक होता है।",
                meaningEnglish = "While perceptive intellects arrive at similar truths, mindless crowds also conform blindly.",
                meaningHindi = "सच्चे विचारकों का निष्कर्ष भले एक जैसा हो, लेकिन बिना सोचे भीड़ का एकमत होना समझदारी नहीं है।",
                example = "Both research labs independently invented the identical transformer optimization; great minds think alike.",
                category = "Wisdom"
            ),
            ProverbEntity(
                id = "proverb_64",
                englishText = "Honesty gives peace of mind.",
                hindiText = "सत्य और निष्कपटता के मार्ग पर चलने से अंतर्मन को शांति और आत्मसंतोष मिलता है।",
                hindiEquivalent = "सांच को आंच नहीं।",
                meaningEnglish = "Truthfulness frees the spirit from deceit, paranoia, and the exhausting burden of living a falsehood.",
                meaningHindi = "ईमानदार व्यक्ति को झूठ छिपाने का तनाव नहीं रहता और वह निर्भय होकर जीता है।",
                example = "Disclosing the telemetry vulnerability transparently restored client trust and gave peace of mind.",
                category = "Philosophy"
            ),
            ProverbEntity(
                id = "proverb_65",
                englishText = "When sorrow is asleep, wake it not.",
                hindiText = "बीते हुए दुखों और पुराने विवादों को कुरेदकर दोबारा जीवित नहीं करना चाहिए।",
                hindiEquivalent = "गड़े मुर्दे मत उखाड़ो।",
                meaningEnglish = "Do not unnecessarily rekindle past grievances, healed wounds, or dormant conflicts.",
                meaningHindi = "जो कटु प्रसंग बीत चुके हैं और शांत हो गए हैं, उन्हें दोबारा चर्चा में लाकर पीड़ा नहीं बढ़ानी चाहिए।",
                example = "The departments resolved their dispute last quarter; do not bring up old grudges—when sorrow is asleep, wake it not.",
                category = "Wisdom"
            )
        )
    }
}
