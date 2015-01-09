//
// Created by Mohammad Sadegh Rasooli on 1/7/15.
// Copyright (c) 2015 Mohammad Sadegh Rasooli. All rights reserved.
//

#include "unicode_manager.h"
#include "iostream"

using namespace std;
vector<string> unicode_manager::prefixes(string word, int len) {
	vector<string> prefixes;

	int index=0;
	int i=0;
	bool odd=false;

	while(index<len){
		if(i==word.length())
			break;
		char ch=word.at(i);
		if(!odd && isascii(ch)){
			prefixes.push_back(word.substr(0,i+1));
			index+=1;
		}
		else{
			if(odd){
				odd=false;
				prefixes.push_back(word.substr(0,i+1));
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

	while(index<len){
		if(i<0)
			break;
		char ch=word.at(i);
		if(!odd && isascii(ch)){
			suffixes.push_back(word.substr(i));
			index+=1;
		}
		else{
			if(odd){
				odd=false;
				suffixes.push_back(word.substr(i));
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
