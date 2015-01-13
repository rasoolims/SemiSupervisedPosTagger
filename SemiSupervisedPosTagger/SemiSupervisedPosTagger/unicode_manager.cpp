//
// Created by Mohammad Sadegh Rasooli on 1/7/15.
// Copyright (c) 2015 Mohammad Sadegh Rasooli. All rights reserved.
//

#include "unicode_manager.h"
#include <algorithm>

using namespace std;
vector<string> unicode_manager::prefixes(string word, int len) {
	vector<string> prefixes;

	int index=0;
	int i=0;
	bool odd=false;


	string lowercased_word=word;
	std::transform(lowercased_word.begin(), lowercased_word.end(), lowercased_word.begin(), ::tolower);

	while(index<len){
		if(i==lowercased_word.length())
			break;
		char ch=lowercased_word.at(i);
		if(!odd && isascii(ch)){
			prefixes.push_back(lowercased_word.substr(0,i+1));
			index+=1;
		}
		else{
			if(odd){
				odd=false;
				prefixes.push_back(lowercased_word.substr(0,i+1));
				index++;
			} else
				odd=true;
		}
		i++;
		if(index==len)
			break;
	}

	return prefixes;
}


vector<string> unicode_manager::suffixes(string word, int len) {
	vector<string> suffixes;

	int index=0;
	int i=(int)word.length()-1;
	bool odd=false;

	string lowercased_word=word;
	std::transform(lowercased_word.begin(), lowercased_word.end(), lowercased_word.begin(), ::tolower);

	while(index<len){
		if(i<0)
			break;
		char ch=lowercased_word.at(i);
		if(!odd && isascii(ch)){
			suffixes.push_back(lowercased_word.substr(i));
			index+=1;
		}
		else{
			if(odd){
				odd=false;
				suffixes.push_back(lowercased_word.substr(i));
				index++;
			} else
				odd=true;
		}
		i--;
		if(index==len)
			break;
	}

	return suffixes;
}
