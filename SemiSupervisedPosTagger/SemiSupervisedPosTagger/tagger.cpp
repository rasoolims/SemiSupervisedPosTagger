//
// Created by Mohammad Sadegh Rasooli on 1/9/15.
// Copyright (c) 2015 Mohammad Sadegh Rasooli. All rights reserved.
//

#include "tagger.h"
#include "file_manager.h"
#include "viterbi.h"

vector<string> tagger::tag(string line, index_maps maps, averaged_perceptron classifier, bool const is_decode, string const delim,bool const use_beam_search, int const beam_size) {
	sentence sen=file_manager::get_sentence_from_line(line, maps.string_dic, delim);
	return tag(sen,maps,classifier,is_decode,use_beam_search,beam_size);
}

vector<string> tagger::tag(sentence sen, index_maps maps, averaged_perceptron classifier, bool const is_decode, bool const use_beam_search, int const beam_size) {
	vector<int> tags= tag(sen,classifier,is_decode,use_beam_search,beam_size);

	vector<string> str_tags;
	for(int i=0;i<tags.size();i++)
		str_tags.push_back(maps.reverse_dict[tags.at(i)]);
	return str_tags;
}


vector<int> tagger::tag(sentence sen, averaged_perceptron classifier, bool const is_decode, bool const use_beam_search, int const beam_size) {
	//todo write beam search
	return use_beam_search? viterbi::viterbi_third_order(sen, classifier,  is_decode)
			:viterbi::viterbi_third_order(sen, classifier, is_decode);
}
