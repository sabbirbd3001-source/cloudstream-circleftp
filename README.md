# Circle FTP CloudStream Extension Repository

এই রিপোজিটরিটি **CloudStream 3** অ্যাপের জন্য **Circle FTP (http://new.circleftp.net/)** এর একটি স্বয়ংসম্পূর্ণ এক্সটেনশন/প্রোভাইডার। 

এটি ব্যবহার করে CloudStream অ্যাপের ভিতরেই Circle FTP-এর সমস্ত মুভি, টিভি সিরিজ, হিন্দি ডাবড মুভি এবং অ্যানিমেশন সরাসরি সার্চ করে দ্রুতগতির BDIX স্পিডে বাফারলেস স্ট্রিম করা যাবে।

---

## 🌟 ফিচারসমূহ (Features)

- **হোমপেজ এক্সপ্লোরার:** Latest Releases, Most Visited, English Movies, Hindi Movies, South Indian Dubbed, TV Series ইত্যাদি ক্যাটাগরি স্বয়ংক্রিয়ভাবে লোড হবে।
- **লাইভ সার্চ (Search):** যেকোনো মুভি বা সিরিজের নাম লিখে সার্চ করলেই Circle FTP-এর রেজাল্ট চলে আসবে।
- **কমপ্লিট মেটাডেটা:** পোস্টার, রিলিজ সাল, স্টোরিলাইন/প্লট, রেজোলিউশন/কোয়ালিটি (1080p, 720p, 4K ইত্যাদি)।
- **টিভি সিরিজ ও এপিসোড সাপোর্ট:** একাধিক সিজন এবং এপিসোড স্বয়ংক্রিয়ভাবে সাজানো থাকবে।
- **ডিরেক্ট BDIX স্ট্রিমিং:** কোনো রি-ডিরেক্ট বা বিজ্ঞাপনের ঝামেলা ছাড়াই সরাসরি হাই-স্পিড BDIX লিঙ্ক থেকে ExoPlayer-এ প্লে হবে।

---

## 🚀 ক্লাউড স্ট্রিম অ্যাপে যুক্ত করার নিয়ম (Setup Guide)

### ধাপ ১: আপনার GitHub অ্যাকাউন্টে রিপোজিটরি তৈরি ও পুশ করুন
১. GitHub-এ একটি নতুন **Public Repository** তৈরি করুন (যেমন: `cloudstream-circleftp`)।  
২. এই ফোল্ডারের সমস্ত কোড আপনার GitHub রিপোজিটরিতে পুশ করুন:

```bash
git init
git add .
git commit -m "Initial commit for Circle FTP CloudStream plugin"
git branch -M main
git remote add origin https://github.com/<আপনার-ইউজারনেম>/<আপনার-রিপো-নাম>.git
git push -u origin main
```

---

### ধাপ ২: GitHub Actions পারমিশন চালু করুন
প্লাগইনটি নিজে থেকেই কম্পাইল হয়ে `.cs3` ফাইল এবং `plugins.json` তৈরি করার জন্য:
1. আপনার GitHub রিপোজিটরির **Settings**-এ যান।
2. বাঁপাশের মেনু থেকে **Actions** > **General**-এ ক্লিক করুন।
3. নিচে স্ক্রোল করে **Workflow permissions** সেকশনে যান।
4. **Read and write permissions** সিলেক্ট করুন এবং **Save** বাটনে ক্লিক করুন।

> কোড পুশ করার পর GitHub Actions স্বয়ংক্রিয়ভাবে আপনার কোড বিল্ড করবে এবং একটি `builds` ব্রাঞ্চ তৈরি করে সেখানে প্লাগইন ও `plugins.json` আপলোড করবে।

---

### ধাপ ৩: ক্লাউড স্ট্রিমে রিপোজিটরি লিঙ্ক যুক্ত করুন

1. আপনার অ্যান্ড্রয়েড ফোনে **CloudStream 3** অ্যাপটি ওপেন করুন।
2. নিচের দিকে ডানপাশে থাকা **Settings (সেটিংস)** আইকনে ট্যাপ করুন।
3. **Extensions** সেকশনে যান।
4. **Repositories** অপশনে ট্যাপ করে **Add Repository**-তে ক্লিক করুন।
5. নিচের মতো করে তথ্য দিন:
   - **Repository Name:** `Circle FTP`
   - **Repository URL:**
     ```
     https://raw.githubusercontent.com/sabbirbd3001-source/cloudstream-circleftp/builds/plugins.json
     ```
6. **Download / Save** দিন।
7. এখন Extensions লিস্টে গিয়ে **Circle FTP** প্লাগইনটির পাশে থাকা ডাউনলোড বা ইনস্টল বাটনে চাপুন।

ইনস্টল হয়ে গেলে CloudStream-এর হোমপেজ ও সার্চবারে Circle FTP-এর সব ভিডিও চলে আসবে!

---

## 📌 গুরুত্বপূর্ণ নোট
- Circle FTP মূলত বাংলাদেশের **BDIX** নেটওয়ার্কের আওতাভুক্ত। তাই ভিডিওগুলো প্লে ও স্ট্রিম করার জন্য আপনার ইন্টারনেট কানেকশন BDIX সাপোর্টেড হতে হবে।

---

## 🛠️ প্রজেক্ট স্ট্রাকচার

```
.
├── .github/workflows/
│   └── build.yml               # GitHub Actions CI/CD অটো-কম্পাইলার
├── CircleFTP/
│   ├── build.gradle.kts        # প্লাগইন মেটাডেটা ও সেটিংস
│   └── src/main/kotlin/com/circleftp/
│       ├── CircleFTPPlugin.kt   # প্লাগইন এন্ট্রি পয়েন্ট
│       └── CircleFTPProvider.kt # Circle FTP স্ক্র্যাপার ও API হ্যান্ডলার
├── gradle/
│   └── wrapper/
├── build.gradle.kts            # রুট Gradle কনফিগারেশন
├── settings.gradle.kts         # সাবপ্রজেক্ট সেটিংস
└── README.md
```
