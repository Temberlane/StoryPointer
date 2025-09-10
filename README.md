# Story Point Predictor  

## Overview  
This project uses machine learning to **predict Agile story points** from user stories (e.g., Jira issues, GitHub issues). By training on public datasets of labeled stories, the model learns to approximate how teams assign effort values to stories.  

The goal is not to replace human judgment, but to **provide guidance** and **calibration** for estimation, especially when teams want to reduce bias or speed up sprint planning.  

---

## Features  
- **Text Embedding Representation**: Convert story descriptions and acceptance criteria into vector embeddings using pretrained language models.  
- **Neural Network Regression**: Train a regression model that predicts story points from embeddings.  
- **Cross-Validation**: Apply k-fold cross-validation to reduce overfitting and measure generalization performance.  
- **Distribution Output**: For any given story, produce a distribution graph of viable story point estimates (not just a single number).  
- **Fine-Tuning Option**: Adapt the model on a small set of team-specific data to capture a particular team’s story-pointing culture.  

---

## Why This Matters  
- Story points are subjective and vary across teams.  
- Historical datasets are small and noisy.  
- This project explores whether embedding-based ML models can capture **relative complexity trends** and provide useful input into estimation conversations.  


## References
For raw data
https://solar.cs.ucl.ac.uk/pdf/tawosi2022msr.pdf
https://github.com/morakotch/datasets/tree/master

This paper suggests that current methods 