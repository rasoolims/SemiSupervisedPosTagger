//
// Created by Mohammad Sadegh Rasooli on 1/7/15.
// Copyright (c) 2015 Mohammad Sadegh Rasooli. All rights reserved.
//

#include "file_manager.h"
#include "iostream"
#include "set"
#include "unicode_manager.h"

using namespace std;
index_maps file_manager::create_indexMaps(string file_path,const string delim) {
	cout << "creating index maps..."<<flush;
	set<string> words;
	set<string> tags;

	// 0 for <start>
	// 1 for <end>
	int index = 2;
	int tag_size;
	unordered_map<string, int> string_dic;
	vector<string> reverse_dict;

	// first indexing the tags
	ifstream reader(file_path);
	if(!reader.good()){
		cout << "file "<< file_path<< " does not exit"<<endl;
		throw exception();
	}


	while (!reader.eof()) {
		string line;
		getline(reader, line);
		if (line.length() > 0) {
			istringstream iss(line);
			do {
				string sub;
				iss >> sub;
				if (sub.length() > 0) {
					string tag = sub.substr(sub.rfind(delim) + 1);
					tags.insert(tag);
				}
			} while (iss);
		}
	}
	reader.close();

	set<string>::iterator it;
	for (it = tags.begin(); it != tags.end(); ++it) {
		string tag = *it;
		if (string_dic.count(tag) <= 0) {
			string_dic[tag] = index++;
		}
	}
	tag_size=index;

	// then indexing other strings
	reader.open(file_path);
	while (!reader.eof()) {
		string line;
		getline(reader, line);
		if (line.length() > 0) {
			istringstream iss(line);
			do {
				string sub;
				iss >> sub;
				if (sub.length() > 0) {
					string word = sub.substr(0, sub.rfind(delim));
					words.insert(word);

					vector<string> prefixes = unicode_manager::prefixes(word, 4);
					for(int i=0;i<prefixes.size();i++) {
						words.insert(prefixes.at(i));
					}

					vector<string> suffixes = unicode_manager::suffixes(word, 4);
					for(int i=0;i<suffixes.size();i++) {
						words.insert(suffixes.at(i));
					}
				}
			} while (iss);
		}
	}


	for (it = words.begin(); it != words.end(); ++it) {
		string word = *it;
		if (string_dic.count(word) <= 0) {
			string_dic[word] = index++;
		}
	}

	vector<string> rv_str;
	vector<int> rv_int;
	for(auto kv:string_dic){
		rv_str.push_back(kv.first);
		rv_int.push_back(kv.second-2);
	}

	reverse_dict.push_back("<START>");
	reverse_dict.push_back("<STOP>");
	for(int i=2;i<string_dic.size();i++) {
			reverse_dict.push_back(rv_str.at(rv_int.at(i)));
	}

	cout << "done!\n"<<flush;

	return index_maps(string_dic, reverse_dict,tag_size);
}

vector<sentence> file_manager::read_sentences(string file_path, unordered_map<string, int> string_dict,const string delim) {
	vector<sentence> sentences;
	ifstream reader(file_path);

	if(!reader.good()){
		cout << "file "<< file_path<< " does not exist"<<endl;
		throw exception();
	}

	cout << "reading the file "<<file_path<<"..."<<flush;
	int count=0;
	while (!reader.eof()) {
		string line;
		getline(reader, line);
		if (line.length() > 0) {
			count++;
			if(count%100==0)
				cout <<count<<"..."<<flush;
			sentence sen=get_sentence_from_line(line, string_dict,delim);
			if(sen.length>0)
			sentences.push_back(sen);
		}
	}
	cout <<"done!"<<endl;

	return sentences;
}

sentence file_manager::get_sentence_from_line(const string line, unordered_map<string, int> string_dict,const string delim) {
	istringstream iss(line);
	vector<string> words;
	vector<string> tags;
	int len = 0;
	do {
		string sub;
		iss >> sub;
		if (sub.length() > 0) {
			int ind=sub.rfind(delim);
			if(ind>0) {
				string word = sub.substr(0, ind);
				string tag = sub.substr(ind + 1, sub.length());
				words.push_back(word);
				tags.push_back(tag);
				len++;
			}
		}
	} while (iss);
	return sentence(words, tags, len, string_dict);
}
