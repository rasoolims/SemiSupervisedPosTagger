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

sentence::~sentence() {

}
