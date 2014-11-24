Twitter Dataset

Release: 1.0 (20/06/2014)
_______________________________________________________________________________

Data : http://www.mpi-inf.mpg.de/~smukherjee/data/twitter-data.tar.gz

Papers: 
1. http://people.mpi-inf.mpg.de/~smukherjee/research/coling12-discourse-sa.pdf
2. http://people.mpi-inf.mpg.de/~smukherjee/research/cikm2012-twisent.pdf

The data has been used in the following papers:

1. Subhabrata Mukherjee and Pushpak Bhattacharyya
Sentiment Analysis in Twitter with Lightweight Discourse Analysis
Proc. of the 24th International Conference on Computational Linguistics (COLING). 2012

2. Subhabrata Mukherjee, Akshat Malu, Balamurali A.R. and Pushpak Bhattacharyya
TwiSent: A Multi-Stage System for Analyzing Sentiment in Twitter
Proc. of The 21st ACM Conference on Information and Knowledge Management (CIKM). 2012

Contact: 
smukherjee@mpi-inf.mpg.de

_______________________________________________________________________________

---- FILES ----

- README.txt: This README file.

- For each file, the following details are shown below :
-- description
-- schema 
-- example record from each file

---- DATA DESCRIPTION AND FORMAT ----

1. Manually-Annotated-Tweets.tsv (8510 tweets manually classified in 4 classes)

-- description : 8510 tweets are collected based on a total of around 2000 different entities from 20 different domains. The following domains are used for crawling data: Movie, Restaurant, Television, Politics, Sports, Education, Philosophy, Travel, Books Technology, Banking & Finance, Business, Music, Environment, Computers, Automobiles, Cosmetics brands, Amusement parks and Eatables and History. These are manually annotated by 4 annotators into four classes - positive, negative, objective-not-spam and objective-spam. The objective-not-spam category contains tweets which are objective in nature but are not spams. The objective-spam category contains spam tweets. For definition of spam and non-spam tweets please refer to our CIKM paper.
-- schema : tweet (tab) class
-- example record : Taking my daughter to the cinema for the first time, 'film' is tinkerbell rather than citizen Kane/shawshank redemption, but it's a start!	positive

---------------------

2. Auto-Annotated-Positive-Tweets.txt (7354 positive tweets)

-- description : We create an artificial dataset using hashtags. The Twitter API is used to collect another set of 7354 tweets based on hashtags. Hashtags #positive, #joy, #excited, #happy etc. are used to collect tweets bearing positive sentiment
-- schema : tweet
-- example record : RT @AlliPaul21: @BrennanJLeBlanc YOU CAN DOOO ITTTT!  #positive


3. Auto-Annotated-Negative-Tweets.txt (7865 negative tweets)

-- description : Hashtags like #negative, #sad, #depressed, #gloomy, #disappointed etc. are used to collect negative tweets.
-- schema : tweet
-- example record : #thatawkwardmoment when people think cuban is mexican. Aha! #negative
_______________________________________________________________________________


BibTex Entry:

@inproceedings{mukherjee2012discourse,
  author    = {Subhabrata Mukherjee and
               Pushpak Bhattacharyya},
  title     = {Sentiment Analysis in Twitter with Lightweight Discourse
               Analysis},
  booktitle = {COLING},
  year      = {2012},
  pages     = {1847-1864},
  ee        = {http://aclweb.org/anthology/C/C12/C12-1113.pdf},
}

@inproceedings{mukherjee2012twisent,
 author = {Mukherjee, Subhabrata and Malu, Akshat and A.R., Balamurali and Bhattacharyya, Pushpak},
 title = {TwiSent: A Multistage System for Analyzing Sentiment in Twitter},
 booktitle = {Proceedings of the 21st ACM International Conference on Information and Knowledge Management},
 series = {CIKM '12},
 year = {2012},
 pages = {2531--2534},
 url = {http://doi.acm.org/10.1145/2396761.2398684},
} 

