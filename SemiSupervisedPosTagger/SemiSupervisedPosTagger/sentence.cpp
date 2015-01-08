//
// Created by Mohammad Sadegh Rasooli on 1/7/15.
// Copyright (c) 2015 Mohammad Sadegh Rasooli. All rights reserved.
//

#include "sentence.h"
#include "iostream"
#include "unicode_manager.h"

sentence::sentence(vector<string> words, vector<string> tags, int length, unordered_map<string,int> string_dict) {
	this->length=length;
	for(int i=0;i<length;i++){
		string word= words.at(i);
		string tag=tags.at(i);

		if(string_dict.count(word)>0)
			this->words.push_back(string_dict[word]);
		else
			this->words.push_back(-1);

		if(string_dict.count(tag)>0)
			this->tags.push_back(string_dict[tag]);
		else
			this->tags.push_back(-1);

		vector<string> prefixes = unicode_manager::prefixes(word, 4);
		vector<string> suffixes = unicode_manager::suffixes(word, 4);

		vector<int> int_prefixes;
		vector<int> int_suffixes;

		for(int i=0;i<prefixes.size();i++) {
			string prefix=prefixes.at(i);
			if(string_dict.count(prefix)>0)
				int_prefixes.push_back(string_dict[prefix]);
			else
				int_prefixes.push_back(-1);
		}

		for(int i=0;i<suffixes.size();i++) {
			string suffix=suffixes.at(i);
			if(string_dict.count(suffix)>0)
				int_suffixes.push_back(string_dict[suffix]);
			else
				int_suffixes.push_back(-1);
		}
		this->prefixes.push_back(int_prefixes);
		this->suffixes.push_back(int_suffixes);

		bool is_uppercase=false;
		bool has_number=false;
		bool has_hyphen=false;
		for(int i=0;i<word.length();i++){
			char ch=word.at(i);
			if(!is_uppercase && isupper(ch))
				is_uppercase=true;
			if(!has_hyphen && ch=='-')
				has_hyphen=true;
			if(!has_number && isdigit(ch))
				has_number=true;
			if(has_hyphen && has_number && is_uppercase)
				break;
		}

		this->contains_hyphen.push_back(has_hyphen);
		this->contains_number.push_back(has_number);
		this->contains_uppercase_letter.push_back(is_uppercase);
	}
}


// the features are identical to Ratnaparkhi (1996) but as in Collins (2002) rare words are ignored
// all features are used for both rare and frequent words
vector<int>* sentence::get_features(int position, int prev_tag, int prev2_tag,int feat_size) {
	vector<int>* features=new vector<int>[feat_size];


	int current_word=0;
	if(position>=0 && position<length)
		current_word=words.at(position);
	else if(position>=length)
		current_word=1;

	int index=0;


	// current word
	features[index++].push_back(current_word);

	//prefixes
	if(position>=0 && position<length) {
		vector<int> pr=prefixes.at(position);
		for (int i = 0; i < pr.size();i++)
			features[index].push_back(pr.at(i));
	}
	index++;

	//suffixes
	if(position>=0 && position<length) {
		vector<int> sf=suffixes.at(position);
		for (int i = 0; i < sf.size();i++)
			features[index].push_back(sf.at(i));
	}
	index++;

	// contain number
	if(position>=0 && position<length) {
		features[index].push_back((int)contains_hyphen.at(position));
	}
	index++;

	// contain uppercase letter
	if(position>=0 && position<length) {
		features[index].push_back((int)contains_uppercase_letter.at(position));
	}
	index++;

	// contain hyphens
	if(position>=0 && position<length) {
		features[index].push_back((int)contains_number.at(position));
	}
	index++;

	// unigram tag
	features[index++].push_back(prev_tag);

	// bigram tags
	int bigram=(prev2_tag<<10) +  prev_tag;
	features[index++].push_back(bigram);

	// previous word
	int prev_word=0;
	int prev_pos=position-1;
	if(prev_pos>=0 && prev_pos<length) {
		prev_word=words.at(prev_pos);
	}
	features[index++].push_back(prev_word);

	// second previous word
	int prev2_word=0;
	int prev2_pos=position-2;
	if(prev2_pos>=0 && prev2_pos<length) {
		prev2_word=words.at(prev2_pos);
	}
	features[index++].push_back(prev2_word);

	// next word
	int next_word=1;
	int next_pos=position+1;
	if(next_pos<length){
		next_word=words.at(next_pos);
	}
	features[index++].push_back(next_word);

	// second next word
	int next2_word=1;
	int next2_pos=position+2;
	if(next2_pos<length){
		next_word=words.at(next2_pos);
	}
	features[index++].push_back(next2_word);

	return features;
}

sentence::~sentence() {

}
