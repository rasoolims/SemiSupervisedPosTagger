//
// Created by Mohammad Sadegh Rasooli on 1/7/15.
// Copyright (c) 2015 Mohammad Sadegh Rasooli. All rights reserved.
//

#include "file_manager.h"
#include "iostream"
#include "set"
#import "unicode_manager.h"

using namespace std;
index_maps file_manager::create_indexMaps(string file_path) {
	set<string> words;
	set<string> tags;

	// 0 for <start>
	// 1 for <end>
	int index = 2;
	unordered_map<string, int> string_dic;
	vector<string> reverse_dict;

	// first indexing the tags
	ifstream reader(file_path);
	while (!reader.eof()) {
		string line;
		getline(reader, line);
		if (line.length() > 0) {
			istringstream iss(line);
			do {
				string sub;
				iss >> sub;
				if (sub.length() > 0) {
					string tag = sub.substr(sub.rfind("/") + 1);
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
			reverse_dict.push_back(tag);
		}
	}

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
					string word = sub.substr(0, sub.rfind("/"));
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
			reverse_dict.push_back(word);
		}
	}


	return index_maps(string_dic, reverse_dict);
}

vector<sentence> file_manager::read_sentences(string file_path, unordered_map<string, int> string_dict) {
	vector<sentence> sentences;
	ifstream reader(file_path);
	while (!reader.eof()) {
		string line;
		getline(reader, line);
		if (line.length() > 0) {
			sentences.push_back(get_sentence_from_line(line, string_dict));
		}
	}

	return sentences;
}

sentence file_manager::get_sentence_from_line(const string line, unordered_map<string, int> string_dict) {
	istringstream iss(line);
	vector<string> words;
	vector<string> tags;
	int len = 0;
	do {
		string sub;
		iss >> sub;
		if (sub.length() > 0) {
			string word = sub.substr(0, sub.rfind("/"));
			string tag = sub.substr(sub.rfind("/") + 1, sub.length());
			words.push_back(word);
			tags.push_back(tag);
			len++;
		}
	} while (iss);
	return sentence(words, tags, len, string_dict);
}
