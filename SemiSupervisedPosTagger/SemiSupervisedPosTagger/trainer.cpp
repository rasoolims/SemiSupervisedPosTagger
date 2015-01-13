//
// Created by Mohammad Sadegh Rasooli on 1/8/15.
// Copyright (c) 2015 Mohammad Sadegh Rasooli. All rights reserved.
//

#include "trainer.h"
#include "file_manager.h"
#include "assert.h"
#include "iostream"
#include "tagger.h"
#include <ctime>

using namespace std;
void trainer::train(string const train_file_path, string const dev_file_path, string const model_path, int const feat_size, string const delim, int const max_iterations) {
	index_maps maps = file_manager::create_indexMaps(train_file_path, delim);   //todo maps should be saved to memory

	// reading train and dev sentences to a vector
	vector<sentence> train_sentences = file_manager::read_sentences(train_file_path, maps.string_dic, delim);
	vector<sentence> dev_sentences = file_manager::read_sentences(dev_file_path, maps.string_dic, delim);

	averaged_perceptron classifier(maps.tag_size, feat_size);
	for (int iter = 1; iter <= max_iterations; iter++) {
		cout << "\niter: " << iter << endl;
		int corr = 0;
		int all = 0;
		//iterating over all training sentences
		for (int s = 0; s < train_sentences.size(); s++) {
			sentence sen = train_sentences.at(s);
			if ((s + 1) % 100 == 0)
				cout << (s + 1) << " " << flush;
			vector<int> predicted_tags = tagger::tag(sen, classifier, false);

			assert(predicted_tags.size() == sen.tags.size());

			bool same = true;
			for (int t = 0; t < predicted_tags.size(); t++) {
				int predicted = predicted_tags.at(t);
				int gold = sen.tags.at(t);
				if (predicted != gold) {
					same = false;
				} else
					corr++;
				all++;
			}

			// updating weights
			if (!same) {
				for (int t = 0; t < predicted_tags.size(); t++) {
					int predicted = predicted_tags.at(t);
					int gold = sen.tags.at(t);

					int predicted_prev_tag = 0;
					int predicted_prev2_tag = 0;
					int gold_prev_tag = 0;
					int gold_prev2_tag = 0;

					if (t > 0) {
						predicted_prev_tag = predicted_tags.at(t - 1);
						gold_prev_tag = sen.tags.at(t - 1);
						if (t > 1) {
							predicted_prev2_tag = predicted_tags.at(t - 2);
							gold_prev2_tag = sen.tags.at(t - 2);
						}
					}

					if (gold != predicted || predicted_prev_tag != gold_prev_tag || predicted_prev2_tag != gold_prev2_tag) {
						vector<int> predicted_features = sen.get_features(t, predicted_prev2_tag, predicted_prev_tag);
						vector<int> gold_features = sen.get_features(t, gold_prev2_tag, gold_prev_tag);

						for (int f = 0; f < feat_size; f++) {
							int pfeat = predicted_features.at(f);
							int gfeat = gold_features.at(f);

							if (pfeat != -1)
								classifier.change_weight(predicted, f, pfeat, -1);
							if (gfeat != -1)
								classifier.change_weight(gold, f, gfeat, +1);
						}
					}
				}


				int predicted_prev_tag = 0;
				int predicted_prev2_tag = 0;
				int gold_prev_tag = 0;
				int gold_prev2_tag = 0;
				int t = predicted_tags.size();

				if (t > 0) {
					predicted_prev_tag = predicted_tags.at(t - 1);
					gold_prev_tag = sen.tags.at(t - 1);
					if (t > 1) {
						predicted_prev2_tag = predicted_tags.at(t - 2);
						gold_prev2_tag = sen.tags.at(t - 2);
					}
				}

				if (predicted_prev_tag != gold_prev_tag || predicted_prev2_tag != gold_prev2_tag) {
					vector<int> predicted_features = sen.get_features(t, predicted_prev2_tag, predicted_prev_tag);
					vector<int> gold_features = sen.get_features(t, gold_prev2_tag, gold_prev_tag);

					for (int f = feat_size - 2; f < feat_size; f++) {
						int pfeat = predicted_features.at(f);
						int gfeat = gold_features.at(f);

						if (pfeat != -1)
							classifier.change_weight(1, f, pfeat, -1);


						if (gfeat != -1)
							classifier.change_weight(1, f, gfeat, +1);
					}
				}
			}

			classifier.increment_iteration();
		}
		float accuracy = (float) corr * 100.0 / all;
		cout << endl << "train accuracy: " << accuracy << endl;

		inf_struct info(classifier);
		averaged_perceptron perceptron(info.tag_size, info.feat_size, info.averaged_weights);
		cout << "\ndecoding..." << flush;
		corr = 0;
		all = 0;
		int exact = 0;

		int start_s = clock();

		for (int s = 0; s < dev_sentences.size(); s++) {
			sentence sen = dev_sentences.at(s);
			if ((s + 1) % 10 == 0)
				cout << (s + 1) << " " << flush;
			vector<int> predicted_tags = tagger::tag(sen, perceptron, true);

			assert(predicted_tags.size() == sen.tags.size());

			bool same = true;
			for (int t = 0; t < predicted_tags.size(); t++) {
				int predicted = predicted_tags.at(t);
				int gold = sen.tags.at(t);
				if (predicted != gold) {
					same = false;
				} else
					corr++;
				all++;
			}
			if (same)
				exact++;
		}
		int stop_s = clock();

		float duration = (float) (stop_s - start_s) / double(CLOCKS_PER_SEC) * 1000;
		duration /= dev_sentences.size();

		cout << "\nduration " << duration << endl;

		accuracy = (float) corr * 100.0 / all;
		float exact_match = (float) exact * 100.0 / dev_sentences.size();
		cout << "dev accuracy is " << accuracy << endl;
		cout << "dev exact match is " << exact_match << endl;

		//delete perceptron;
		//delete  info;
	}
}


void trainer::save_model(string const model_path, index_maps const maps,  inf_struct const info) {
	//todo should write index_maps first

	ofstream writer;

	writer.open(model_path,ios::binary|ios::out);
	writer<<info.tag_size<<endl;
	writer<<info.feat_size<<endl;

	for(int i=0;i<info.tag_size;i++) {
		for (int j = 0; j < info.feat_size; j++) {
			for(auto kv:info.averaged_weights[i][j]){
					writer << i << endl;
					writer << j << endl;
					writer << kv.first << endl;
					writer << kv.second << endl;
			}
		}
	}


	writer.flush();
	writer.close();


}
