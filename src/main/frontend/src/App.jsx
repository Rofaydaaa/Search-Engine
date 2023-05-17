import React, { useState } from 'react';
import { Container } from 'react-bootstrap';
import SearchEngine from './Components/SearchEngine';
import {BrowserRouter,Router, Route, Routes } from 'react-router-dom';
import api from './api/axiosConfig'
import './App.css';
import ResultPage from './Components/ResultPage';

function App() {
    const [searchText, setSearchText] = useState('');
    const [searchResults, setSearchResults] = useState([]);
    const getResults = async (text) => {
        if(text){
            const response = await api.post('/results', {suggestion: text});
            console.log(response.data);
            setSearchResults(response.data);
        }
    }
    return (
        <BrowserRouter>
            <Container>
                    <div className={"App-container"}>
                        <Routes>
                            <Route path="/" exact element={<SearchEngine handleSearch={setSearchText} getResults={getResults}/>}/>
                            <Route path="/Results" exact element={<ResultPage searchResults={searchResults} getResults={getResults} handleSearch={setSearchText} searchText={searchText}/>}/>
                        </Routes>
                    </div>
            </Container>
        </BrowserRouter>
    );
}

export default App;